package com.example.vangogh;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/*
    Reference for creating this class:
    https://medium.com/tensorflow/using-tensorflow-lite-on-android-9bbc9cb7d69d
 */
public class MusicalChordClassifierNet extends Classifier{
      protected Interpreter interpreter;
      private final String model_path = "";
      public MusicalChordClassifierNet()
      {
//          interpreter = new Interpreter(loadModelFile(model_path));
          /**
           * There’s a helper function for this in the TensorFlow Lite sample on GitHub.
           * Just ensure that getModelPath() returns a string that points to a file in your assets folder, and the model should load.
           */
      }

      //TODO: Fix the data types of the parameters as we see necessary
      public void infer(String data, double[] probability_array)
      {
          interpreter.run(data, probability_array);
      }

//    @Override
//    protected String getModelPath() {
//        return "model.tflite";
//    }
//
//    @Override
//    protected String getLabelPath() {
//        return "labels.txt";
//    }
    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        //TODO: Implement this method for loading the model
//        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(getModelPath());
//        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
//        FileChannel fileChannel = inputStream.getChannel();
//        long startOffset = fileDescriptor.getStartOffset();
//        long declaredLength = fileDescriptor.getDeclaredLength();
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        return null;
  }
}



