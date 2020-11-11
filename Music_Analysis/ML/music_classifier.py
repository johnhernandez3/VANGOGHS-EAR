<<<<<<< HEAD
<<<<<<< HEAD
import os, pathlib

# from utils.settings import get_project_root, find
# from utils.settings import *
# from settings import *

=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
import tensorflow as tf

import matplotlib.pyplot as plt
import numpy as np
<<<<<<< HEAD
<<<<<<< HEAD

import os.path as path
from pathlib import Path, PurePath
import re

=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
import seaborn as sns


from tensorflow.keras.layers.experimental import preprocessing
from tensorflow.keras import layers
from tensorflow.keras import models

<<<<<<< HEAD
<<<<<<< HEAD
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
    if path.isdir(chord_path):
        # this is the Guitar_Chords parent folder
        for f in Path.iterdir(chord_path):
            if f.is_dir():
                yield path.abspath(f)
        
    # path= find('Guitar_Chords', get_project_root())
    # return path

def chord_files(path=chords_paths(), chord_name='a'):

    # for chord_audio_path in findPattern(path, pattern=chord_name):
    #     yield chord_audio_path
    for chord in Path.iterdir(path):
        if chord.is_file():
            yield chord


=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
def  decode_audio(audio_binary):
    audio, _  = tf.audio.decode_wav(audio_binary)

    return tf.squeeze(audio, axis=-1)

<<<<<<< HEAD
<<<<<<< HEAD
def detect_chord_name(str_filename):

    return

def get_label(file_path):
    if file_path.is_file():
        return PurePath(file_path).name
    else:
        raise ValueError(f"Could not find file:{file_path}")
        # return None

=======
=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
def get_label(file_path):

    return

<<<<<<< HEAD
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
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

def chord_classifier_model(input_shape, norm_layer, num_labels):
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
<<<<<<< HEAD
<<<<<<< HEAD
    return history
=======
    return
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======
    return
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec

def confusion_matrix(model, labels,y_true,y_pred):

    confusion_mtrx = tf.math.confusion_matrix(y_true,y_pred)
    plt.figure(figsize=(10,8))
    sns.heatmap(confusion_mtrx, xticklabels=labels,yticklabels=labels,annot=True, fmt='g')
    plt.xlabel('Prediction')
    plt.ylabel('Label')
    plt.show()