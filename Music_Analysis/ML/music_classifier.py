import tensorflow as tf
import typing
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
    chord_path = Path(setup()) #may need to change this for *nix sys
    chord_path = chord_path / 'ML' / 'Guitar_Chords'
    if path.exists(chord_path):
        if path.isdir(chord_path):
            # this is the Guitar_Chords parent folder
            for f in Path.iterdir(chord_path):
                if f.is_dir():
                    yield path.abspath(f)
    else:
        raise ValueError(f"Path:{chord_path}\nwas not found in the system!")
        
    # path= find('Guitar_Chords', get_project_root())
    # return path

def chord_files(path=chords_paths(), chord_name='a'):

    # for chord_audio_path in findPattern(path, pattern=chord_name):
    #     yield chord_audio_path
    for chord in Path.iterdir(path):
        if chord.is_file():
            yield chord

def  decode_audio(audio_binary):
    audio, _  = tf.audio.decode_wav(audio_binary)
    return tf.squeeze(audio, axis=-1)

def extract_chord_class(chord_filename:str)->str:
    pattern = r"""([abcdefg]|[ABCDEFG])[7]?[#]?(([M][a][j])|([M][i][n]))?"""
    matcher = re.compile(pattern)
    match = matcher.match(chord_filename)
    if match is not None:
        return match.string[match.start(0):match.end(0)]
    return None

def get_label(file_path):
    '''
        TODO: Doing too many things in this method, refactor to only do a single operation'''
    
    # if file_path.is_dir():
    #     for chord_audio in Path.iterdir(file_path):
    #         if chord_audio.is_file():
    #             yield extract_chord_class(PurePath(chord_audio).name)

    # if file_path.is_file():
    #             return extract_chord_class(PurePath(file_path).name)
    if file_path.is_file() or file_path.is_dir():
        return extract_chord_class(PurePath(file_path).name)
    else:
        raise ValueError(f"Invalid file path provided:{file_path}")

def get_waveform_and_label(file_path):
    label = get_label(file_path)
    audio_binary = tf.io.read_file(file_path)
    waveform = decode_audio(audio_binary)
    return label, waveform


def get_spectrogram(waveform):

    # this is zero padding for files whose sample rate is less than 16kHz
    zero_padding = tf.zeros([16000] -  tf.shape(waveform), dtype=tf.float32)

    waveform = tf.cast(waveform, tf.float32)
    equal_length = tf.concat([waveform, zero_padding],0)
    spectrogram = tf.signal.stft( equal_length, frame_length=255,frame_step=128)
    return spectrogram

def spectrogram_dataset(files=[]):

    spectrogram_ds = [ get_spectrogram(file) for file in files]
    return

def preprocess():
    AUTOTUNE = tf.data.experimental.AUTOTUNE
    files_ds = [chord_file for chord_file in chord_files()]
    files_ds = tf.data.Dataset.from_tensor_slices(files_ds)
    waveforms_ds = files_ds.map(get_waveform_and_label, num_parallel_calls=AUTOTUNE)
    return waveforms_ds

def get_label_from_slice(waveform_ds):

    for label, wv in waveform_ds:
        label = label.numpy().decode('utf-8')
        yield label

def waveform_to_spectrogram_ds(waveform_ds):

    for label, wv in waveform_ds:
        label = label.numpy().decode('utf-8')
        spect = get_spectrogram(wv)
        yield label, spect

def get_spectrogram_and_label_id(audio, label, num_labels):
    spectrogram = get_spectrogram(audio)
    spectrogram = tf.expand_dims(spectrogram, -1)
    label_id = tf.argmax(label == num_labels)
    return spectrogram, label_id

def chord_classifier_model(input_shape, norm_layer, num_labels=10):
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
    
def train(train_dataset, validation_data, model, epochs=10):
    history = model.fit(
        train_dataset,
        validation_data=validation_data,
        epochs=epochs,
        callbacks=tf.keras.callbacks.EarlyStopping(verbose=1,patience=2),
    )
    return

def confusion_matrix(model, labels,y_true,y_pred):

    confusion_mtrx = tf.math.confusion_matrix(y_true,y_pred)
    plt.figure(figsize=(10,8))
    sns.heatmap(confusion_mtrx, xticklabels=labels,yticklabels=labels,annot=True, fmt='g')
    plt.xlabel('Prediction')
    plt.ylabel('Label')
    plt.show()