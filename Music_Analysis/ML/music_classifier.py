import os, pathlib


import tensorflow as tf
import typing
import matplotlib.pyplot as plt
import numpy as np

import os.path as path
from pathlib import Path, PurePath
import re

import seaborn as sns


from tensorflow.keras.layers.experimental import preprocessing
from tensorflow.keras import layers
from tensorflow.keras import models

ROOT_DIR = None

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

def chords_paths():
    '''
        Yields the paths to the Chord Classes if the subdirectory ~ML/Guitar_Chords/~
        exists in the system.
    '''
    chord_path = Path(setup()) 
    chord_path = chord_path / 'ML' / 'Guitar_Chords'
    if path.isdir(chord_path):
        # this is the Guitar_Chords parent folder
        for f in Path.iterdir(chord_path):
            if f.is_dir():
                yield path.abspath(f)

def chord_files(path=chords_paths(), chord_name='a'):

    # for chord_audio_path in findPattern(path, pattern=chord_name):
    #     yield chord_audio_path
    for chord in Path.iterdir(path):
        if chord.is_file():
            yield chord


def  decode_audio(audio_binary):
    audio, _  = tf.audio.decode_wav(audio_binary)
    return tf.squeeze(audio, axis=-1)

def detect_chord_name(str_filename):


def get_label(file_path):
    if file_path.is_file():
        return PurePath(file_path).name
    else:
        raise ValueError(f"Could not find file:{file_path}")
        # return None

def get_spectrogram(waveform):

    # this is zero padding for files whose sample rate is less than 16kHz
    # zero_padding = tf.zeros([16000] -  tf.shape(waveform), dtype=tf.float32)
    # if tf.shape(waveform) > 0:
    waveform = tf.cast(waveform, tf.float32)
    spectrogram = tf.signal.stft( waveform, frame_length=255,frame_step=128)
    return spectrogram
    # else:
    #     pass

def spectrogram_dataset(files=[]):

    spectrogram_ds = [ get_spectrogram(file) for file in files]
    return

def preprocess():
    AUTOTUNE = tf.data.experimental.AUTOTUNE
    files_ds = chords_files()
    files_ds = tf.data.Dataset.from_tensor_slices(files_ds)
    waveforms_ds = files_ds.map(get_waveform_and_label, num_parallel_calls=AUTOTUNE)
    return waveforms_ds

def preprocess_dataset(files):
    AUTOTUNE = tf.data.experimental.AUTOTUNE
    # files_ds = (chord_file for chord_file in files)
    # for idx, f in enumerate(files_ds):
    #     print(f"{idx} Type of file: {type(f)}\nFilename:{f}\n")
    files_ds = tf.data.Dataset.from_tensor_slices(files)
    # for idx, f in enumerate(files_ds):
    #     print(f"{idx} Type of file: {type(f)}\nFilename:{f}\n")
    waveforms_ds = files_ds.map(get_waveform_and_label, num_parallel_calls=AUTOTUNE)
    spectrogram_ds = waveforms_ds.map(get_spectrogram_and_label_id, num_parallel_calls=AUTOTUNE )
    return spectrogram_ds

def filenames_to_tensor_slices(filenames):
    files = tf.data.Dataset.from_tensor_slices(filenames)
    return files

def get_label_from_slice(waveform_ds):

    for label, wv in waveform_ds:
        label = label.numpy().decode('utf-8')
        yield label

def waveform_to_spectrogram_ds(waveform_ds):

    for label, wv in waveform_ds:
        label = label.numpy().decode('utf-8')
        spect = get_spectrogram(wv)
        yield label, spect

def get_spectrogram_and_label_id(audio, label, num_labels=10):
    spectrogram = get_spectrogram(audio)
    spectrogram = tf.expand_dims(spectrogram, -1)
    label_id = tf.argmax(label == num_labels)
    return spectrogram, label_id

def divide_ds(files):
    
    # train, val, test = [] , [] , []
    # if len(files) %2 == 0:
        # then even
    mid = len(files) // 2
    # train , val, test = files[0:mid-1],files[mid:mid//2 + mid -1], files[mid//2 + mid:mid + mid -1]
    train = files[:mid-1]
    val = files[mid:mid//2 + mid -1]
    test = files[mid//2 + mid//1:mid//1 + mid//1 -1]
        # pass
    # else:
    #     #odd
    #     mid = len(files) / 2
    #     train , val, test = files[0:mid-1],files[mid,mid//2 + mid -1], files[mid//2 + mid,mid + mid -1]
    #     pass
    # if len(fs) % 3 == 0:
    #     train, val, test = fs[0:len(fs)//3 - 1],fs[len(fs)//3: 2 * len(fs)//3 -1], fs[ 2 * len(fs)//3 : len(fs) -1]
    # else:
    #     size = len(fs)
    #     train, val, test = fs[0: size//3 - 1],fs[size//3: 2 * size//3 -1], fs[ 2 * size //3 ::]
    
    return train, val, test

def chord_classifier_model(input_shape, norm_layer=None, num_labels=10):
    norm_layer = preprocessing.Normalization()
    norm_layer.adapt(spectrogram_dataset().map(lambda x,_: x))
    model = models.Sequential(
        [
            layers.Input(shape=input_shape),
            preprocessing.Resizing(32,32),
            norm_layer,
            layers.Conv2D(32,3,activation='relu'),
            layers.Conv2D(64,3,activation='relu'),
            layers.MaxPool2D(),
            layers.Dropout(0.25),
            layers.Flatten(),
            layers.Dense(128,activation='relu'),
            layers.Dropout(0.5),
            layers.Dense(num_labels),
        ]
    )

    model.summary()

    model.compile(
        optimizer=tf.keras.optimizers.Adam(),
        loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
        metrics=['accuracy'],
    )
    return model

def init_training():
    train, val, test = divide_ds(files=chords_files())
    train, val, test = filenames_to_tensor_slices(train),filenames_to_tensor_slices(val),filenames_to_tensor_slices(test),
    train_ds = train.map(get_waveform_and_label, num_parallel_calls=AUTOTUNE)
    train_ds = train_ds.map(get_spectrogram_and_label_id,num_parallel_calls=AUTOTUNE)
    val_ds = preprocess_dataset(val)
    test_ds = preprocess_dataset(test)

    # in_shape = None
    for  spectrogram, _ in train_ds.take(1):
        in_shape = spectrogram.shape
    model = chord_classifier_model(input_shape=in_shape)
    hist = train_model(train_ds, val_ds,model, )

    plot_model_loss(hist)

def test_one_file():

    chord = Path(setup())
    chord = chord / 'Guitar_Chords' / 'a'/ 'a.wav'

    train = str(chord)

    train_ds = get_waveform_and_label(train)
    train_ds = get_spectrogram_and_label_id(train_ds, 'a')

    model = chord_classifier_model(input_shape=train_ds[0].take(1)[0].shape)
    
    return model

def train_model(train_dataset, validation_data, model, epochs=10):
    batch_size = 64

    train_dataset = train_dataset.batch(batch_size)
    validation_data = validation_data.batch(batch_size)
    
    train_dataset = train_dataset.cache().prefetch(AUTOTUNE)
    validation_data = validation_data.cache().prefetch(AUTOTUNE)
    

    history = model.fit(
        train_dataset,
        validation_data=validation_data,
        epochs=epochs,
        callbacks=tf.keras.callbacks.EarlyStopping(verbose=1,patience=2),
    )
    return history

def confusion_matrix(model, labels,y_true,y_pred):

    confusion_mtrx = tf.math.confusion_matrix(y_true,y_pred)
    plt.figure(figsize=(10,8))
    sns.heatmap(confusion_mtrx, xticklabels=labels,yticklabels=labels,annot=True, fmt='g')
    plt.xlabel('Prediction')
    plt.ylabel('Label')
    plt.show()

def main():
    # init_training()
    test_one_file()
    return

if __name__ == "__main__":
    main()