package com.example.vangogh;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class AudioRecorder extends Fragment {

    private final int REQUEST_READ_STORAGE = 0;
    private final int REQUEST_WRITE_STORAGE = 1;
    private final int REQUEST_RECORD_AUDIO = 2;
    private static final String Output_File = "Sample_File";
    private static final String TAG = "AUDIO RECORDER FRAG";
    MediaRecorder recorder;
    View view;
    Button microphone_button;

    private String OutputFilePath()
    {
        //TODO Implement the output file path to store the Audio recordings from Mic.
        // Alternatively, we could use streams of audio because we don't really want to store the audio.
        String res = "";

        return res;
    }


    private void setupMediaRecorder(String file_path)
    {
        recorder = new MediaRecorder();
        try{
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(file_path);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Could not setup Audio Recorder due to exception:"+ e);
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Ask if Audio recording permission is granted
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {
            //If not ask the user for the permission
            requestPermissions(
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        } else {
            Log.e(TAG, "PERMISSION GRANTED");

            try {
                setupMediaRecorder(this.OutputFilePath());
                recorder.prepare();
            } catch(Exception e)
            {
                Log.e(TAG, "Error preparing Mic:" + e);
                e.printStackTrace();
            }
        }


        view = inflater.inflate(R.layout.audio_recorder, container , false);

        microphone_button = (Button) view.findViewById(R.id.microphone_button);


        microphone_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                recorder.start();
                Toast.makeText(getActivity(),  TAG, Toast.LENGTH_SHORT).show();
                recorder.stop();
                recorder.reset();
            }

        });

        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if(recorder != null)
        {
            try{
                recorder.release();
            }catch(Exception e)
            {
                Log.e(TAG, "Exception while releasing AudioRecorder object:"+  e);
                e.printStackTrace();
            }
        }

        Log.d(TAG, "AudioRecorder.onDestroyView method was called.");
    }


}
