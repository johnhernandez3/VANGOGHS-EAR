# Beat tracking exampleS
from __future__ import print_function
import librosa
import librosa.display
import numpy as np
import matplotlib.pyplot as plt
from typing import List, Any, Iterable
import warnings

from utils.settings import get_project_root, find

filename = ""
nix_filename = ""
# filename = r"C:\Users\johnm\git\MusicAnalysis\Music_Analysis\Megaman_ZX_-_Green_Grass_Gradiation_NITRO_Remix (1).wav"

# nix_filename = r"Megaman_ZX_-_Green_Grass_Gradiation_NITRO_Remix (1).wav"

def load_music(filename, dir=get_project_root()):

    filename= find(path=dir, name=filename)

    y, sample_rate = librosa.load(filename)

    D = librosa.stft(y)

    log_pow = librosa.amplitude_to_db(np.abs(D**2), ref=np.max)

    return y, sample_rate,D , log_pow

def show_music_log_pow(data=load_music(nix_filename)):

    log_pow_data = data[3]

    # plt.figure()
    # librosa.display.specshow(log_pow_data,x_axis='time', y_axis='log')
    # plt.colorbar()
    # plt.show()

    plot_music(data=log_pow_data)
    return

def extract_frequency_data(music_data=load_music(filename=nix_filename)):
    '''This only works when calling load_music function where data[2] is sample_rate'''
    sample_rate = music_data[2]
    return librosa.fft_frequencies(sample_rate)

def plot_music(data, x_axis='time', y_axis='log'):
    plt.figure()
    librosa.display.specshow(data, x_axis=x_axis, y_axis=y_axis)
    plt.colorbar()
    plt.show()
    return

warnings.filterwarnings("ignore",category=UserWarning)

# TODO: this needs to be abstracted away so we can avoid conflicts between both our systems

# 1. Get the file path to the included audio example

# filename = r"C:\Users\johnm\git\MusicAnalysis\Music_Analysis\Megaman_ZX_-_Green_Grass_Gradiation_NITRO_Remix (1).wav"

# 2. Load the audio as a waveform `y`
#    Store the sampling rate as `sr`
# y, sr = librosa.load(filename)

# D = librosa.stft(y)

# log_pow = librosa.amplitude_to_db(np.abs(D**2), ref=np.max)

# E = librosa.fft_frequencies(sr)

# plt.figure()
# librosa.display.specshow(E, x_axis = 'time', y_axis = 'log')
# plt.colorbar()

# plt.figure()
# librosa.display.specshow(log_pow, x_axis = 'time', y_axis = 'log')
# plt.colorbar()






# plt.show()

# 3. Run the default beat tracker
# tempo, beat_frames = librosa.beat.beat_track(y=y, sr=sr)

# print('Estimated tempo: {:.2f} beats per minute'.format(tempo))

# 4. Convert the frame indices of beat events into timestamps
# beat_times = librosa.frames_to_time(beat_frames, sr=sr)

# python -c "import numpy; print(numpy.version.version)" ; to change switch modules from numpy to whatever needed
# pip install funcsigs==version.version.version ; to switch, change version to a number
# intended output: 136.00 beats per minute using wav included


if __name__ == "__main__":
    # print(f"Running  Sample file:{nix_filename}")
    show_music_log_pow()