import os
import pathlib
from pathlib import Path


import os, sys, threading
import os.path as path
from pathlib import Path, PurePath
import re
from platform import system

import inspect

import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns
import tensorflow as tf

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

model_path = Path(setup())
model_path = model_path / 'Saved_Model'

model_path = model_path/'chords_classifier_model'

# Convert the model
converter = tf.lite.TFLiteConverter.from_saved_model(str(model_path)) # path to the SavedModel directory
tflite_model = converter.convert()

# Save the model.
with open('model.tflite', 'wb') as f:
  f.write(tflite_model)

