package io_devices;

import android.media.MediaRecorder;
import android.util.Log;

import utils.Device;

/**
 * Class for representing the IO Device of a Microphone for the AudioRecorder class.
 */
public class Microphone implements Device
{

    private final String TAG  = "MIC";
    private MediaRecorder recorder;

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