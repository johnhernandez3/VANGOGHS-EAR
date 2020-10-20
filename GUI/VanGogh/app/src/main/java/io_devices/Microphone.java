package io_devices;

import android.media.MediaRecorder;
import android.util.Log;

import utils.Device;


public class Microphone extends Device
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
        this.recorder = new MediaRecorder();

        try{
            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(file_path);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }catch(Exception e){
            Log.e(TAG,"Error Encountered while setting up mic."+ e);

            e.printStackTrace();

        }
    }


    public boolean start()
    {
        try{
            recorder.prepare();
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

    public boolean stop()
    {
        try{
            recorder.stop();
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


    public boolean reset(String file_path)
    {
        try{
            recorder.reset();
            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(file_path);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

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
