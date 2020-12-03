package io_devices;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.wavFile.WavFileException;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.common.ops.NormalizeOp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;
import java.util.stream.Collectors;

import utils.Device;
import utils.MapEntryComparator;

/**
 * Class for representing the IO Device of a Microphone for the AudioRecorder class.
 */
public class Microphone implements Device
{

    private final String TAG  = "MIC";
    private MediaRecorder recorder;
    private AudioRecord wav_recorder;

    private File  directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/recordings/");
    private String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private long recording_time = 0;
    private Timer timer;

    private int RECORDER_BITS = 16;
    private int RECORDER_SAMPLERATE = 16000;


    private String recording_timeString="";
    private final String AUDIO_FILE_FORMAT = ".wav";
    private final String TEMP_FILE_FORMAT = "temp_rec.raw";

    private int RECORDER_CHANNELS = android.media.AudioFormat.CHANNEL_IN_STEREO;
    private int RECORDER_AUDIO_ENCODING = android.media.AudioFormat.ENCODING_PCM_16BIT;
    float audioData[][];//size of 2

    private  int buffer_size = 0;
    private Thread recorderThread;
    private boolean isRecording = false;

    private String output = "";

    public Microphone()
    {
        try{
            // create a File object for the parent directory
            File recorderDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/soundrecorder/");
            // have the object build the directory structure, if needed.
            recorderDirectory.mkdirs();
        }catch (Exception e){
        e.printStackTrace();
    }

        if(directory.exists()){
            int count = directory.listFiles().length;
            output = Environment.getExternalStorageDirectory().getAbsolutePath() + "/soundrecorder/recording"+count+".mp3";
        }

        buffer_size = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING)*3;

        audioData =  new float[2][buffer_size];
        timer = new Timer();
    }


    /**
     * Establishes the connection with Mic hardware, sets the Audio format, Encoder and file storage path.
     *
     * @param file_path The filepath where the system will store the audio file created
     */
    public Microphone(String file_path)
    {
        Log.d(TAG, "Using file:"+file_path);
        this.recorder = new MediaRecorder();

        try{
            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(file_path);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();
        }catch(Exception e){
            Log.e(TAG,"Error Encountered while setting up mic."+ e);

            e.printStackTrace();

        }
    }

    public boolean start_recording_wav()
    {
        this.wav_recorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                buffer_size
        );

        int state = wav_recorder.getState();

        if(state == 1) wav_recorder.startRecording();

        isRecording = true;

        recorderThread  = new Thread()
        {
            @Override
            public void run()
            {
                writeAudioDataToFile();
            }

        };

        recorderThread.start();
        startTimer();

        return isRecording;
    }

    private String getTempFilename()
    {
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(filepath, TEMP_FILE_FORMAT);
        if (tempFile.exists()) tempFile.delete();

        return file.getAbsolutePath() + "/" + TEMP_FILE_FORMAT;
    }


    private void writeAudioDataToFile() {
        byte data[] = new byte[buffer_size];
        String filename = getTempFilename();
        FileOutputStream os = null;
        try {
            os = new  FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int read = 0;
        if (null != os) {
            while (isRecording) {
                read = wav_recorder.read(data, 0, buffer_size, AudioRecord.READ_BLOCKING);
                if (read > 0) {
                }
                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean stop_recording_wav( Context context){
        /*mediaRecorder?.stop()
        mediaRecorder?.release()
        */
        if (null != wav_recorder) {
            isRecording = false;
            int i = wav_recorder.getState();
            if (i == 1) wav_recorder.stop();
            wav_recorder.release();
            wav_recorder = null;
            recorderThread = null;
        }
        stopTimer();
        resetTimer();

        String fileName = getFilename();
        copyWaveFile(getTempFilename(), fileName);
        deleteTempFile();
        initRecorder();

//        classifyRecording(fileName, context);

        return !isRecording;

    }

    private String getFilename()
    {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if(!file.exists())file.mkdirs();
        return file.getAbsolutePath().toString() + "/" + SystemClock.currentThreadTimeMillis() + AUDIO_FILE_FORMAT ;
    }

    private void copyWaveFile(String input, String output)
    {
        FileInputStream f_in;FileOutputStream f_out;
        long total_audio_len = 0;
        long total_length = total_audio_len + 36; //for headers
        long sample_rate = (long) RECORDER_SAMPLERATE;
        int channels = 2;

        long byte_rate = (long) RECORDER_BITS * RECORDER_SAMPLERATE *channels / 8;

        byte data[]  = new byte[buffer_size];

        try{

            f_in = new FileInputStream(input);
            f_out = new FileOutputStream(output);

            total_audio_len = f_in.getChannel().size();
            total_length = total_audio_len + 36;//TODO: MAY BE A BUG HERE
            WriteWaveFileHeader(f_out, total_audio_len, total_length, sample_rate, channels, byte_rate);

            while(f_in.read(data) != -1)
            {
                f_out.write(data);
            }

            f_in.close();
            f_out.close();

        }catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, long channels,
            long byteRate)
    {
        byte header [] = new byte[44];
        header[0] =(byte) 'R';// RIFF/WAVE header
        header[1] =(byte) 'I';
        header[2] =(byte) 'F';
        header[3] = (byte)'F';
        header[4] = (byte)(totalDataLen & 0xff);
        header[5] =(byte) (totalDataLen >> 8 & 0xff);
        header[6] = (byte) (totalDataLen >> 16 & 0xff);
        header[7] = (byte)(totalDataLen >> 24 & 0xff);
        header[8] = (byte)'W';
        header[9] = (byte)'A';
        header[10] =(byte) 'V';
        header[11] = (byte)'E';
        header[12] =(byte) 'f'; // 'fmt ' chunk
        header[13] =(byte) 'm';
        header[14] = (byte)'t';
        header[15] = (byte)' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1 ;// format = 1
        header[21] = 0;
        header[22] = (byte)channels;
        header[23] = 0;
        header[24] = (byte)(longSampleRate & 0xff);
        header[25] = (byte)(longSampleRate >> 8 & 0xff);
        header[26] =(byte) (longSampleRate >> 16 & 0xff);
        header[27] = (byte)(longSampleRate >> 24 & 0xff);
        header[28] = (byte)(byteRate & 0xff);
        header[29] = (byte)(byteRate >> 8 & 0xff);
        header[30] =(byte) (byteRate >> 16 & 0xff);
        header[31] = (byte)(byteRate >> 24 & 0xff);
        header[32] = (byte)(2 * 16 / 8); // block align
        header[33] = (byte)0;
        header[34] = (byte)RECORDER_BITS ;// bits per sample
        header[35] =(byte) 0;
        header[36] = (byte)'d';
        header[37] = (byte)'a';
        header[38] =(byte) 't';
        header[39] = (byte)'a';
        header[40] =(byte) (totalAudioLen & 0xff);
        header[41] =(byte) (totalAudioLen >> 8 & 0xff);
        header[42] = (byte)(totalAudioLen >> 16 & 0xff);
        header[43] = (byte)(totalAudioLen >> 24 & 0xff);

        try{
            out.write(header, 0, 44);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void deleteTempFile()
    {
        File file = new File(getTempFilename());
        file.delete();
    }

    private void  initRecorder() {
        recorder =new  MediaRecorder();

        if(directory.exists()){
            int count = directory.listFiles().length;
            output = Environment.getExternalStorageDirectory().getAbsolutePath() + "/soundrecorder/recording"+count+".mp3";
        }

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(output);
    }

    private void startTimer()
    {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                recording_time += 1;
            }
        },1000, 1000);
        updateDisplay();
    }

    private void stopTimer(){
        timer.cancel();
    }


    private void resetTimer() {
        timer.cancel();
        recording_time = 0;
        recording_timeString = "00:00";
    }

    private void updateDisplay(){
        int minutes = (int) recording_time / (60);
        int seconds = (int) recording_time % 60 ;
        String str = String.format("%d:%02d", minutes, seconds);
        recording_timeString = str;
    }

    private String getRecordingTime() {return recording_timeString;}


    private void classifyRecording(String fileName, Context context)
    {

        Runnable loadRunnable =  new  Runnable( {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            // Creates a toast pop-up.
            // This is to know if this runnable is running on UI thread or not!
            try {
                SharedPreferences sharedPreferences = null;


                sharedPreferences = context.getSharedPreferences(GENRE_PREFERENCES, Context.MODE_PRIVATE);


                String audioFilePath = fileName;

                try {

                    int defaultSampleRate =  -1; //-1 value implies the method to use default sample rate

                    int defaultAudioDuration = -1 ; //-1 value implies the method to process complete audio duration


                    JLibrosa jLibrosa = new JLibrosa();
                    float audioFeatureValues[] = jLibrosa.loadAndRead(audioFilePath, defaultSampleRate, defaultAudioDuration)
                    ArrayList<Float> audioFeatureValuesList = jLibrosa.loadAndReadAsList(
                            audioFilePath,
                            defaultSampleRate,
                            defaultAudioDuration
                    );


                    ArrayList<List<Float>> splitSongs  = splitSongs(audioFeatureValuesList, 0.5);

                    ArrayList<String> predictionList  = new ArrayList<String>();
                    for(int i=0; i <  splitSongs.size(); i++){
                        float audioArr [] =(float[]) splitSongs[i].toArray();

                        float melSpectrogram[][]=
                        jLibrosa.generateMelSpectroGram(audioArr, 22050, 1024, 128, 256)


                        String prediction  = loadModelAndMakePredictions(melSpectrogram, context);
                        predictionList.add(prediction);
//                        println("test");
                    }

//                    println(predictionList.groupingBy { it }.eachCount().filter { it.value > 1 })

//                    List predList = predictionList.groupingBy { it }.eachCount();
                    Map predList = predictionList.stream().collect(Collectors.toMap(Function.identity(), e-> 1,Math::addExact));
//                    List sortedPredList = predList.entrySet().sortedByDescending { it.value }.associate { it.toPair()};
                    String sortedPredList = predList.entrySet().stream().sorted(new MapEntryComparator()).iterator().next();
//                    String predValue = sortedPredList.entrySet().iterator().next();
//
//                     SharedPreferences.Editor  editor = sharedPreferences.edit();
//                    editor.putString(fileName, predValue);
//                    editor.commit();


                }
                catch(WavFileException e){
                    e.printStackTrace();
                   Log.e(TAG, e.getMessage());
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread predictionThread = new  Thread(loadRunnable);
        predictionThread.start();

    }

    protected String loadModelAndMakePredictions(float meanMFCCValues[][] , Context context) throws IOException {



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



        //The for at the end is the amount of bytes that a float occupies in Java
        // taken from the mfcc mean array
        ByteBuffer byteBuffer  = ByteBuffer.allocate(4*meanMFCCValues.length* meanMFCCValues[0].length);

    for(int i= 0;i <  meanMFCCValues.length; i++){
        float valArray[] = meanMFCCValues[i];
        int inpShapeDim[] = {1,1,meanMFCCValues[0].length,1};
        TensorBuffer valInTnsrBuffer = TensorBuffer.createDynamic(imageDataType);
        valInTnsrBuffer.loadArray(valArray, inpShapeDim);
        ByteBuffer  valInBuffer = valInTnsrBuffer.getBuffer();
        byteBuffer.put(valInBuffer);
    }

    byteBuffer.rewind();

    //val inpBuffer: ByteBuffer? = convertBitmapToByteBuffer(bitmp)
    TensorBuffer outputTensorBuffer =
            TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)
    //run the predictions with input and output buffer tensors to get probability values across the labels
    tflite.run(byteBuffer, outputTensorBuffer.getBuffer());


    //Code to transform the probability predictions into label values
    String ASSOCIATED_AXIS_LABELS = "labels.txt";
        List<String> associatedAxisLabels  = null ;
    try {
        associatedAxisLabels = FileUtil.loadLabels(context, ASSOCIATED_AXIS_LABELS)
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

    private String getModelPath() {
        String res = "";
        return res;
    }
    public String  getPredictedValue(List<Recognition> predictedList) {
        Recognition top1PredictedValue  = predictedList.get(0);
    return top1PredictedValue.getTitle();
}



    /** Gets the top-k results.  */
    protected List<Recognition> getTopKProbability(Map<String, Float> labelProb ) {
    // Find the best classifications.
    int MAX_RESULTS = 1;
    PriorityQueue<Recognition>  pq = new PriorityQueue(
            MAX_RESULTS,
            Comparator<Recognition> { lhs, rhs -> // Intentionally reversed to put high confidence at the head of the queue.
                    java.lang.Float.compare(rhs.getConfidence(), lhs.getConfidence())
            })
    for (Map.Entry<String, Float> entry : labelProb) {
        pq.add(Recognition("" + entry.getKey(), entry.getKey(), entry.getValue()));
    }

        ArrayList<Recognition> recognitions = new ArrayList();
    int recognitionsSize = Math.min(pq.size(), MAX_RESULTS);
    for (int i = 0; i < recognitionsSize ; i++) {
        recognitions.add(pq.poll());
    }
    return recognitions
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



    /**
     * Initiates the process of storing data from the microphone into the internal file
     * @return boolean representing if it was successful or not
     */
    public boolean start()
    {
        try{
            recorder.start();
        }catch(Exception e)
        {
            Log.e(TAG, "Error while attempting to start recording:"+e);
            e.printStackTrace();

            return false;
        }
        finally {
            return true;
        }
    }

    /**
     * Halts he process of storing data from the microphone into the internal file
     * @return boolean representing if it was successful or not
     */
    public boolean stop()
    {
        try{
            recorder.stop();
            recorder.reset();
            recorder.release();
        }catch(Exception e)
        {
            Log.e(TAG, "Error while attempting to stop recording:"+e);
            e.printStackTrace();
            return false;
        }
        finally {
            return true;
        }
    }

    @Override
    public boolean reset() {
        return false;
    }

    /**
     * Resets he process of storing data from the microphone into the new internal file @param file_path
     * @return boolean representing if it was successful or not
     */
    public boolean reset(String file_path)
    {
        try{
            recorder.reset();
            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(file_path);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();

        }catch(Exception e)
        {
            Log.e(TAG, "Error while resetting microphone:"+e);
            e.printStackTrace();

            return false;
        }
        finally {
            return true;
        }
    }




}
