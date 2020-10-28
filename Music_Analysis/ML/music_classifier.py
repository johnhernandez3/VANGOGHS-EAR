import tensorflow as tf

import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns


from tensorflow.keras.layers.experimental import preprocessing
from tensorflow.keras import layers
from tensorflow.keras import models

def  decode_audio(audio_binary):
    audio, _  = tf.audio.decode_wav(audio_binary)

    return tf.squeeze(audio, axis=-1)

def get_label(file_path):

    return

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
    return

def confusion_matrix(model, labels,y_true,y_pred):

    confusion_mtrx = tf.math.confusion_matrix(y_true,y_pred)
    plt.figure(figsize=(10,8))
    sns.heatmap(confusion_mtrx, xticklabels=labels,yticklabels=labels,annot=True, fmt='g')
    plt.xlabel('Prediction')
    plt.ylabel('Label')
    plt.show()