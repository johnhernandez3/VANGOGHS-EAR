package com.example.vangogh;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorOperator;

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
          super();
//          interpreter = new Interpreter(loadModelFile(model_path));
          /**
           * There’s a helper function for this in the TensorFlow Lite sample on GitHub.
           * Just ensure that getModelPath() returns a string that points to a file in your assets folder, and the model should load.
           */
      }

    public MusicalChordClassifierNet(Activity activity, Device device, int numThreads) {
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

    /**
     * Gets the name of the model file stored in Assets.
     */
    @Override
    protected String getModelPath() {
        return null;
    }

    /**
     * Gets the name of the label file stored in Assets.
     */
    @Override
    protected String getLabelPath() {
        return null;
    }

    /**
     * Gets the TensorOperator to nomalize the input image in preprocessing.
     */
    @Override
    protected TensorOperator getPreprocessNormalizeOp() {
        return null;
    }

    /**
     * Gets the TensorOperator to dequantize the output probability in post processing.
     *
     * <p>For quantized model, we need de-quantize the prediction with NormalizeOp (as they are all
     * essentially linear transformation). For float model, de-quantize is not required. But to
     * uniform the API, de-quantize is added to float model too. Mean and std are set to 0.0f and
     * 1.0f, respectively.
     */
    @Override
    protected TensorOperator getPostprocessNormalizeOp() {
        return null;
    }
}



