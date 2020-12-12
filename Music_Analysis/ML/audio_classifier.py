import tensorflow as tf
import typing,math
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns


from tensorflow.keras.layers.experimental import preprocessing
from tensorflow.keras import layers
from tensorflow.keras import models



import os, sys, threading
import os.path as path
from pathlib import Path, PurePath
import re
from platform import system

import inspect

AUTOTUNE = tf.data.experimental.AUTOTUNE

ROOT_DIR = None

EPOCHS = 10
BATCHES = 64

def setup():
    main_id = None
    for t in threading.enumerate():
        if t.name == 'MainThread':
            main_id = t.ident
            break

    if not main_id:
        raise RuntimeError("Main thread exited before execution")

    current_main_frame = sys._current_frames()[main_id]
    base_frame = inspect.getouterframes(current_main_frame)[-1]

    if system().lower() == 'windows':
        filename = base_frame.filename
    else:
        filename = base_frame[0].f_code.co_filename

    global ROOT_DIR
    ROOT_DIR = os.path.dirname(os.path.abspath(filename))
    return ROOT_DIR

def labels():
    return np.array(['a','am','bm','c','d','dm','e','em','f','g'])

def get_label(file_path):
  parts = tf.strings.split(file_path, os.path.sep)

  # Note: You'll use indexing here instead of tuple unpacking to enable this 
  # to work in a TensorFlow graph.
  return parts[-2] 

def chords_audio_files_path():
    chords_path = Path(setup())
    chords_path = chords_path / 'Guitar_Chords'
    return chords_path

# these are file tensors 
def chords_audio_filenames(chords_path = chords_audio_files_path()):
    print(chords_path)
    filenames = tf.io.gfile.glob(str(chords_path)+'/*/*')
    filenames = tf.random.shuffle(filenames)
    print(filenames)
    print('\n\n')
    return filenames

def split_files(files):
    train, val, test = [], [], []
    return train, val, test

def decode_audio(audio_binary):
    audio, sample_rate = tf.audio.decode_wav(audio_binary,desired_samples=16000)

    print(audio.shape)
    return tf.squeeze(audio,axis=-1)

def get_waveform(file_path):
    audio_binary = tf.io.read_file(file_path)
    waveform = decode_audio(audio_binary)
    return waveform

def get_waveform_and_label(file_path):
    label = get_label(file_path)
    audio_binary = tf.io.read_file(file_path)
    waveform = decode_audio(audio_binary)
    print('Waveform shape:', waveform.shape)
    print()
    return waveform, label

def get_spectrogram(waveform):
    waveform = tf.cast(waveform, tf.float32)

    spectrogram = tf.signal.stft(waveform, frame_length=255, frame_step=128)
    spectrogram = tf.abs(spectrogram)
    print('/n/n Waveform shape:', waveform.shape)
    print('Spectrogram shape:', spectrogram.shape)
    print()
    return spectrogram

def get_spectrogram_and_label_id(audio, label):
    spectrogram = get_spectrogram(audio)
    spectrogram = tf.expand_dims(spectrogram, -1)
    label_id = tf.argmax(label == labels())

    return spectrogram , label_id

def audio_classifier_model(input_shape,norm_layer, num_labels=len(labels())):
    model = models.Sequential([
    layers.Input(shape=input_shape),
    preprocessing.Resizing(32, 32), 
    norm_layer,
    layers.Conv2D(32, 3, activation='relu'),
    layers.Conv2D(64, 3, activation='relu'),
    layers.MaxPooling2D(),
    layers.Dropout(0.25),
    layers.Flatten(),
    layers.Dense(128, activation='relu'),
    layers.Dropout(0.5),
    layers.Dense(num_labels),
])
    
    model.summary()

    model.compile(
    optimizer=tf.keras.optimizers.Adam(),
    loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
    metrics=['accuracy'],
)

    return model

def preprocess_dataset(files):
  files_ds = tf.data.Dataset.from_tensor_slices(files)
  output_ds = files_ds.map(get_waveform_and_label, num_parallel_calls=AUTOTUNE)
  output_ds = output_ds.map(
      get_spectrogram_and_label_id,  num_parallel_calls=AUTOTUNE)

  for spectrogram, label_id in output_ds.take(len(output_ds)):
      print("Spec Shape:",spectrogram.shape)
  return output_ds

def prepare_ds(debug=False):
    filenames = chords_audio_filenames()
    train_files = filenames[:1000]
    val_files = filenames[1001:1001 +499]
    test_files = filenames[-499:]

    if debug:
        filenames = chords_audio_filenames(Path(setup()) / 'sample')
        train_files = filenames
        val_files = filenames
        test_files = filenames

    files_ds = tf.data.Dataset.from_tensor_slices(train_files)
    waveform_ds = files_ds.map(get_waveform_and_label, num_parallel_calls=AUTOTUNE)
    spectrogram_ds = waveform_ds.map(get_spectrogram_and_label_id, num_parallel_calls=AUTOTUNE)

    train_ds = spectrogram_ds
    val_ds = preprocess_dataset(val_files)

    test_ds = preprocess_dataset(test_files)
    return train_ds, val_ds, test_ds



def create_model():
    batch_size = BATCHES
    train_ds, val_ds, test_ds =  prepare_ds()#prepare_ds(debug=True)

    train_ds = train_ds.batch(BATCHES).cache().prefetch(AUTOTUNE)
    val_ds = val_ds.batch(BATCHES).cache().prefetch(AUTOTUNE)

    for spec, _ in val_ds.take(1):
        input_shape = spec.shape
    

    num_labels = len(labels())

    norm_layer = preprocessing.Normalization()
    norm_layer.adapt(val_ds.map(lambda x,_ : x))

    print(f"Input Shape:{input_shape}\n Num Labels:{num_labels}\n Normalization Layer:{norm_layer}\n\n")
    model = audio_classifier_model(input_shape=input_shape, norm_layer=norm_layer, num_labels=num_labels)
    
    model.summary()
    history = fit(model, train_ds, val_ds)
    return model


def fit(model, train_ds, val_ds):
    print(train_ds)
    history = None
    try:
        history = model.fit(train_ds, validation_data = val_ds,epochs=EPOCHS, callbacks=tf.keras.callbacks.EarlyStopping(verbose=1,patience=2),)
    except Exception as e:
        print(f"Exception Occured while training:{e}\n\n with training ds:{train_ds}\n\n")
    return history

def metrics(history):
    metrics = history.history
    plt.plot(history.epoch, metrics['loss'], metrics['val_loss'])
    plt.legend(['loss', 'val_loss'])
    plt.show()

    return

def plot_spectrogram(spectrogram, ax):
    # Convert to frequencies to log scale and transpose so that the time is
    # represented in the x-axis (columns).
    log_spec = np.log(spectrogram.T)
    height = log_spec.shape[0]
    X = np.arange(72500, step=100)
    Y = range(height)
    ax.pcolormesh( X,Y, log_spec)
    return ax

def plot_with_labels(waveform_ds):
    rows = 3
    cols = 3
    n = rows*cols
    fig, axes = plt.subplots(rows, cols, figsize=(10, 12))
    for i, (audio, label) in enumerate(waveform_ds.take(n)):
        r = i // cols
        c = i % cols
        ax = axes[r][c]
        ax = plot_spectrogram(audio.numpy(), ax)
        ax.set_yticks(np.arange(-1.2, 1.2, 0.2))
        label = label.numpy().decode('utf-8')
        ax.set_title(label)

    plt.show()

def plot_audio(waveform, spectrogram):
    fig, axes = plt.subplots(2, figsize=(12, 8))
    timescale = np.arange(waveform.shape[0])
    axes[0].plot(timescale, waveform.numpy())
    axes[0].set_title('Waveform')
    plot_spectrogram(spectrogram.numpy(), axes[1])
    axes[1].set_title('Spectrogram')
    plt.show()

def sample_plot():
    waveform=get_waveform("C:\\Users\\johnm\\git\\VANGOGHS-EAR\\Music_Analysis\\ML\\sample\\a.wav")
    plot_audio(waveform, spectrogram=get_spectrogram(waveform))

def main():
    create_model()
    # sample_plot()
    return

if __name__ == "__main__":
    main()