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
    # labels = np.array(tf.io.gfile.listdir(str(chords_audio_files_path())))
    # return labels
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
    filenames = tf.io.gfile.glob(str(chords_path)+'/*')
    filenames = tf.random.shuffle(filenames)
    print(filenames)
    print('\n\n')
    return filenames

def split_files(files):
    #TODO: Implement splitting logic 
    train, val, test = [], [], []
    return train, val, test

def decode_audio(audio_binary):
    audio, _ = tf.audio.decode_wav(audio_binary)

    return tf.squeeze(audio, axis=-1)

def get_waveform_and_label(file_path):
    label = get_label(file_path)
    audio_binary = tf.io.read_file(file_path)
    waveform = decode_audio(audio_binary)
    print('Waveform shape:', waveform.shape)
    print()
    return waveform, label

def get_spectrogram(waveform):
    padding_size = [44100] - tf.shape(waveform)
    if padding_size < 0:
        padding_size *= -1
    zero_padding = tf.zeros(padding_size, dtype=tf.float32)


    waveform = tf.cast(waveform, tf.float32)
    equal_length = tf.concat([waveform, zero_padding], 0)

    spectrogram = tf.signal.stft(equal_length, frame_length=255, frame_step=256)
    spectrogram = tf.abs(spectrogram)
    print('/n/nWaveform shape:', waveform.shape)
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
    # layers.Input(shape=(input_shape[-2],input_shape[-1]),name='input'),
    preprocessing.Resizing(32, 32), 
    norm_layer,
    layers.Conv2D(64, 3, activation='relu'),
    layers.Conv2D(128, 3, activation='relu'),
    layers.MaxPooling2D(),
    layers.Dropout(0.25),
    layers.Flatten(),
    layers.Dense(256, activation='relu'),
    layers.Dropout(0.5),
    layers.Dense(num_labels),
])
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
  return output_ds

def prepare_ds():
    # filenames = chords_audio_filenames()
    # train_files = filenames[:1000]
    # val_files = filenames[1000:1000 +500]
    # test_files = filenames[-500:]
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
    train_ds, val_ds, test_ds =  prepare_ds()

    train_ds = train_ds.cache().prefetch(AUTOTUNE)
    val_ds = val_ds.cache().prefetch(AUTOTUNE)

    for spec, _ in train_ds.take(1):
        input_shape = spec.shape
    

    num_labels = len(labels())

    norm_layer = preprocessing.Normalization()
    norm_layer.adapt(train_ds.map(lambda x,_ : x))

    model = audio_classifier_model(input_shape, norm_layer, num_labels)
    
    
    model.summary()
    history = fit(model, train_ds, val_ds)
    metrics(history)
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



def main():
    create_model()
    return

if __name__ == "__main__":
    main()