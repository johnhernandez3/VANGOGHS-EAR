package com.example.vangogh;

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

import androidx.fragment.app.Fragment;

public class AudioRecorder extends Fragment {

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
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(file_path);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setupMediaRecorder(this.OutputFilePath());

        try {
            recorder.prepare();
        } catch(Exception e)
        {
            Log.e(TAG, "Error preparing Mic:" + e);
            e.printStackTrace();
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
        recorder.release();
        Log.d(TAG, "AudioRecorder.onDestroyView() was called.");
    }


}
