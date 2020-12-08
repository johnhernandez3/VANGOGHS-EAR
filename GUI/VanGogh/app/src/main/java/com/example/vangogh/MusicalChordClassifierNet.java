package com.example.vangogh;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;

import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.wavFile.WavFileException;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import utils.Device;

/*
    Reference for creating this class:
    https://medium.com/tensorflow/using-tensorflow-lite-on-android-9bbc9cb7d69d
 */
public class MusicalChordClassifierNet extends Classifier{
    private File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecorder/recordings/");

    private final String TAG = "CLASSIFIER";
      protected Interpreter interpreter;
      private final String model_path = "";
      public MusicalChordClassifierNet()
      {
          super();
          //TODO: Implement this
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

    /**
     * Gets the name of the model file stored in Assets.
     */
    @Override
    protected String getModelPath() {
        return "model.tflite";
    }

    /**
     * Gets the name of the label file stored in Assets.
     */
    @Override
    protected String getLabelPath() {
        return "labels.txt";
    }
    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        //TODO: Implement this method for loading the model
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
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

    public void classifyRecording(final String fileName, final Context context)
    {

        final Thread theed = new Thread();
        Runnable loadrun = new Runnable() {
            @Override
            public void run() {
                theed.setPriority(Thread.NORM_PRIORITY);
                // Creates a toast pop-up.
                // This is to know if this runnable is running on UI thread or not!
                try {


                    String audioFilePath = fileName;

                    try {

                        int defaultSampleRate =  -1; //-1 value implies the method to use default sample rate

                        int defaultAudioDuration = -1 ; //-1 value implies the method to process complete audio duration


                        JLibrosa jLibrosa = new JLibrosa();
                        float audioFeatureValues[] = jLibrosa.loadAndRead(audioFilePath, defaultSampleRate, defaultAudioDuration);
                        ArrayList<Float> audioFeatureValuesList = jLibrosa.loadAndReadAsList(
                                audioFilePath,
                                defaultSampleRate,
                                defaultAudioDuration
                        );


                        ArrayList<List<Float>> splitSongs  = splitSongs(audioFeatureValuesList, 0.5);
                        ArrayList<Float[]> temp = new ArrayList<Float[]>();

                        ArrayList<String> predictionList  = new ArrayList<String>();
                        for(int i=0; i <  splitSongs.size(); i++)
                        {

                            temp.add(splitSongs.get(i).toArray(new Float[splitSongs.get(i).size()]));

                            Float[] audioArr = temp.get(i);
                            float[] intermediate = new float[audioArr.length];

                            for(int j=0;  j < audioArr.length; j++)
                            {
                                intermediate[j] = audioArr[j].floatValue();
                            }

                            float melSpectrogram[][]=
                                    jLibrosa.generateMelSpectroGram(intermediate, 22050, 1024, 128, 256);

                            String prediction  = loadModelAndMakePredictions(melSpectrogram, context);
                            predictionList.add(prediction);

                        }
                        StringBuffer buff = new StringBuffer();
                        for(int i = 0; i < predictionList.size(); i++) {
                            buff.append(predictionList.get(i) + "\n\n");
                        }

                        FileManager.writeToLabelsFile(predictionList, getLabelsFilePath());

                        //TODO:Now we can call the ChordFragment's class

                    }
                    catch(WavFileException e){
                        e.printStackTrace();
                        Log.e(TAG, e.getMessage());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //Start the new prediction thread here!
        Thread predictionThread = new  Thread(loadrun);
        predictionThread.start();

    }

    private ArrayList<List<Float>> splitSongs(ArrayList<Float> audioFeatureValuesList, double overlap) {
        int chunk = 3300;
        int offset =( int) (chunk * (1 - overlap)) ;
        int x_shape = audioFeatureValuesList.size();

        float x_max_index = x_shape / chunk;

        float x_max = (x_max_index * chunk) - chunk;

        ArrayList<List<Float>>splitSongValList = new ArrayList<List<Float>>();

        for( int i = 0; i <  x_max + 1; i+=offset){
            splitSongValList.add(audioFeatureValuesList.subList(i, i+chunk));
        }

        return splitSongValList;
    }

    public String getLabelsFilePath()
    {
        return directory.getAbsolutePath() +"labels.txt";
    }


    protected String loadModelAndMakePredictions(float meanMFCCValues[][] , Context context) throws IOException
    {

        String predictedResult = "unknown";


        Interpreter tflite;

        //load the TFLite model in 'MappedByteBuffer' format using TF Interpreter
        MappedByteBuffer tfliteModel  =  FileUtil.loadMappedFile(context, getModelPath());
        /** Options for configuring the Interpreter.  */
        Interpreter.Options tfliteOptions = new  Interpreter.Options();
        tfliteOptions.setNumThreads(2);
        tflite = new Interpreter(tfliteModel, tfliteOptions);

        //get the datatype and shape of the input tensor to be fed to tflite model
        int imageTensorIndex = 0;

        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

        int imageDataShape[] = tflite.getInputTensor(imageTensorIndex).shape();

        //get the datatype and shape of the output prediction tensor from tflite model
        int probabilityTensorIndex = 0;
        int[] probabilityShape =
                tflite.getOutputTensor(probabilityTensorIndex).shape();
        DataType probabilityDataType =
                tflite.getOutputTensor(probabilityTensorIndex).dataType();



        //The 4 at the end is the amount of bytes that a float occupies in Java
        // taken from the mfcc mean array
        ByteBuffer byteBuffer  = ByteBuffer.allocate(63984);
        for(int i= 0;i <  meanMFCCValues.length; i++){
            float[] valArray= meanMFCCValues[i];
            int[] inpShapeDim = {1,1,meanMFCCValues[0].length,1};
            TensorBuffer valInTnsrBuffer = TensorBuffer.createDynamic(imageDataType);
            valInTnsrBuffer.loadArray(valArray, inpShapeDim);
            ByteBuffer  valInBuffer = valInTnsrBuffer.getBuffer();
            byteBuffer.put(valInBuffer);
        }

        byteBuffer.rewind();

        TensorBuffer outputTensorBuffer =
                TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
        //run the predictions with input and output buffer tensors to get probability values across the labels
        tflite.run(byteBuffer, outputTensorBuffer.getBuffer());


        //Code to transform the probability predictions into label values
        String ASSOCIATED_AXIS_LABELS = "labels.txt";
        List<String> associatedAxisLabels  = new ArrayList<>() ;
        try {
            associatedAxisLabels = FileUtil.loadLabels(context, ASSOCIATED_AXIS_LABELS);
        } catch ( IOException e) {
            Log.e("tfliteSupport", "Error reading label file", e);
        }

        //Tensor processor for processing the probability values and to sort them based on the descending order of probabilities
        TensorProcessor probabilityProcessor = new TensorProcessor.Builder().add(new NormalizeOp(0.0f, 255.0f)).build();
        if (null != associatedAxisLabels) {
            // Map of labels and their corresponding probability
            TensorLabel labels = new TensorLabel(
                    associatedAxisLabels,
                    probabilityProcessor.process(outputTensorBuffer)
            );

            // Create a map to access the result based on label
            Map<String, Float> floatMap = labels.getMapWithFloatValue();

            //function to retrieve the top K probability values, in this case 'k' value is 1.
            //retrieved values are storied in 'Recognition' object with label details.
            List<Recognition> resultPrediction  = getTopKProbability(floatMap);

            //get the top 1 prediction from the retrieved list of top predictions
            predictedResult = getPredictedValue(resultPrediction);

        }
        return predictedResult;

    }

    public String  getPredictedValue(List<Recognition> predictedList) {
        Recognition top1PredictedValue  = predictedList.get(0);
        return top1PredictedValue.getTitle();
    }

    /** Gets the top-k results.  */
    protected List<Recognition> getTopKProbability(Map<String, Float> labelProb ) {
        // Find the best classifications.
        int MAX_RESULTS = 1;
        int current = 5;
        Map.Entry<String, Float> current_max = labelProb.entrySet().iterator().next();

        Stack<Map.Entry<String,Float>> recogs = new Stack<>();

        for (Map.Entry<String, Float> entry : labelProb.entrySet()) {

            if(entry.getValue() > current_max.getValue()) {
                recogs.push(entry);
                current_max = entry;
            }

        }

        ArrayList<Recognition> recognitions = new ArrayList();
        for(Map.Entry<String, Float>entry : recogs)
        {
            if(current > 1)
                recognitions.add(new Recognition(entry.getKey(), entry.getKey(), entry.getValue(), new RectF()));
            current--;
        }

        return recognitions;
    }
}



