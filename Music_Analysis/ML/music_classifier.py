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

    zero_padding = tf.zeros([16000] -  tf.shape(waveform), dtype=tf.float32)

    waveform
    return