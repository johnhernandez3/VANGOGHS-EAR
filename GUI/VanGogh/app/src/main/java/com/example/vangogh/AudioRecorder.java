package com.example.vangogh;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.os.Environment;

public class AudioRecorder extends Fragment {

    //used for maintaining track of how many clicks have been performed on the record button
    private int mic_button_clicks=0;//might be problematic in multi-threaded workloads due to possible race condition
                                    //TODO: Consider using atomic integers here
    // Request Codes for acknowledging the permissions requested
    private final int REQUEST_READ_STORAGE = 0;
    private final int REQUEST_WRITE_STORAGE = 1;
    private final int REQUEST_RECORD_AUDIO = 2;

    // String used to store the File being recorded from Mic
    private static final String Output_File = "Sample_File";

    // String to debug using LogCat
    private static final String TAG = "AUDIO RECORDER FRAG";
    private Context context;// Needed for asking about external storage Directory used for storing the files...

    // Android Implementation of media recorder for both audio and video
    MediaRecorder recorder;

    // View that binds the XML Layout to the Java logic
    View view;

    //Button for binding with the XML Button
    Button microphone_button;

    /**
     * Generates the Output File Path for the Audio Recorder to store recorded audio.
     *
     * @param filename the name of the file to be created for storing the recorded audio.
     * @return String representation of the Output File Path
     */
    private String OutputFilePath(String filename)
    {
        String res;
        if(filename != null && filename != " ") {

            context = this.getContext();
            //TODO Implement the output file path to store the Audio recordings from Mic.
            // Alternatively, we could use streams of audio because we don't really want to store the audio.
            res = context.getExternalFilesDir(null).getAbsolutePath() + "/" + filename + ".aac";
        }
        else{
            res = context.getExternalFilesDir(null).getAbsolutePath() + "/" + "sample" + ".aac";
        }
        return res;
    }

    /**
     * Establishes the connection with mic hardware, sets the Audio format, Encoder and file storage path.
     *
     * @param file_path The filepath where the system will store the audio file created
     */
    private void setupMediaRecorder(String file_path)
    {
        //TODO: Fix problem here when entering a session after the first music file is stored...
        // Will cause filename collissions enabling unexpected behaviour...
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
                setupMediaRecorder(this.OutputFilePath("sample"));
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
                switch(mic_button_clicks)
                {
                    case 0:
                        microphone_button.setText("Stop");
                        recorder.start();
                        Toast.makeText(getActivity(),  "Starting Mic Recording", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        microphone_button.setText("Start");
                        Toast.makeText(getActivity(),  "Stopping Mic Recording", Toast.LENGTH_SHORT).show();
                        recorder.stop();
                        recorder.reset();
                        break;

                    default:
                        try {
                            Log.e(TAG, "Unexpected value for Mic Button clicks!");
                            recorder.stop();
                            recorder.reset();
                            microphone_button.setText("Start");
                        }catch(Exception e) {
                            Log.e(TAG, "Tried to stop/reset unavailable MediaRecorder!");
                            e.printStackTrace();

                        }
                        break;

                }
                mic_button_clicks = mic_button_clicks + 1 % 2;// to wrap around for the only two possible states
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
