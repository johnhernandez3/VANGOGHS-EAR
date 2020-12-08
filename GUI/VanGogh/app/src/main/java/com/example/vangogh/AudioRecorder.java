package com.example.vangogh;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import io_devices.Microphone;

public class AudioRecorder extends Fragment
{
//    private Context context;
    private Date todays_date;
    //used for maintaining track of how many clicks have been performed on the record button
    private static int mic_button_clicks=0;//might be problematic in multi-threaded workloads due to possible race condition
                                    //TODO: Consider using atomic integers here

    // Request Codes for acknowledging the permissions requested
    private final int MAX_RECORD_BTN_CLICKS = 2;
    private final int REQUEST_READ_STORAGE = 0;
    private final int REQUEST_WRITE_STORAGE = 1;
    private final int REQUEST_RECORD_AUDIO = 2;

    // String used to store the File being recorded from Mic
    String Output_File = "Sample_File";

    // String to debug using LogCat
    private static final String TAG = "AUDIO RECORDER FRAG";
    private Context context;// Needed for asking about external storage Directory used for storing the files...

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    Microphone mic;


    // View that binds the XML Layout to the Java logic
    View view;

    //Button for binding with the XML Button
    Button microphone_button;

    /**
     * Generates the Output File Path for the Audio Recorder to store recorded audio.
     *
     * @param filename the name of the file to be created for storing the recorded audio.
     * @return String representation of the Output File Path.
     */
    protected String OutputFilePath(String filename)
    {
        String res;
        if(nonEmptyString(filename)) {

            context = this.getContext();
            //TODO: Implement the output file path to store the Audio recordings from Mic.
            // Alternatively, we could use streams of audio because we don't really want to store the audio.
            res = this.OutputFilePath(filename, "3gp");
        }
        else{
            res = this.OutputFilePath("sample","3gp");
        }
        return res;
    }

    /**
     * Verifies if @param text is an empty string or an only whitespace containing string
     * @param text the String we want to verify for emptiness
     * @return boolean representing if the @param text is non-empty
     */
    protected boolean nonEmptyString(String text)
    {
        if(text.trim().length() > 0 && text  != null)
            return true;
        else
            return false;
    }

    /**
     * Generates the Output File Path for the Audio Recorder to store recorded audio.
     *
     * @param filename the name of the file to be created for storing the recorded audio.
     * @param format the file format that the data will be stored as.
     * @return String representation of the Output File Path with specified format
     */
    private String OutputFilePath(String filename, String format)
    {
        String res;
        if(filename != null && filename != " ") {

            context = this.getContext();
            //TODO: Implement the output file path to store the Audio recordings from Mic.
            // Alternatively, we could use streams of audio because we don't really want to store the audio.
            res = context.getExternalFilesDir(null).getAbsolutePath() + "/" + filename + "." +format;
        }
        else{
            res = context.getExternalFilesDir(null).getAbsolutePath() + "/" + "sample" + "."+format;
        }
        return res;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Ask if Audio recording permission is granted
        if (!permissionToRecordAccepted)
        {
            //If not ask the user for the permission
            requestPermissions(
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }

        else {
            Log.d(TAG, "PERMISSION GRANTED:"+Manifest.permission.RECORD_AUDIO);

        }

        context = this.getActivity().getApplication().getBaseContext();


        view = inflater.inflate(R.layout.audio_recorder_fragment, container , false);

        microphone_button = (Button) view.findViewById(R.id.microphone_button);
        microphone_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                switch(mic_button_clicks)
                {
                    case 0:
//                        todays_date = new Date();
//                        SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd-hh:mm:ss");
//                        Output_File = OutputFilePath(ft.format(todays_date));//TODO: fix this, possible race-condition
//                        mic =  new Microphone(Output_File);
                        mic = new Microphone();
//                        Log.d(TAG, "File:"+OutputFilePath(ft.format(todays_date)));

                        microphone_button.setText("Stop");
                        try {
                            Log.d(TAG, "Button clicks:"+mic_button_clicks);
                            mic.start_recording_wav();
                            Toast.makeText(getActivity(), "Starting Mic Recording", Toast.LENGTH_SHORT).show();

//                            mic.stop_recording_wav(context);
                        }catch  (Exception e)
                        {
                            e.printStackTrace();
                            Log.e(TAG, "Error starting Mic Recording");
                        }
                        mic_button_clicks = (mic_button_clicks + 1) % MAX_RECORD_BTN_CLICKS;
                        break;
                    case 1:
                        microphone_button.setText("Start");

                        try {
                            Log.d(TAG, "Button clicks:"+mic_button_clicks);
                            mic.stop_recording_wav(context);
                            FragmentManager frag_man = getParentFragmentManager();
                            Fragment frag = frag_man.findFragmentById(R.id.fragment_container_view);
                            if(frag != null)
                            {
                                //TODO: Fix this so we can see the first label inside the ChordFragment view
                                //Chords is present then
                                //Open file and feed it
                                Scanner fr = new Scanner(new File(mic.getLabelsFilePath()));
                                String first_chord = fr.nextLine();
                                Log.e(TAG, "Current Read Label:"+first_chord);
                                frag_man.beginTransaction().add(R.id.fragment_container_view, new ChordFragment(first_chord) , "CHORDS");
                            }


                        }catch  (Exception e)
                        {
                            e.printStackTrace();
                            Log.e(TAG, "Error stopping MediaRecorder");
                        }

                        mic_button_clicks = (mic_button_clicks + 1) % MAX_RECORD_BTN_CLICKS;
                        break;

                    default:
                        try {
                            Log.e(TAG, "Unexpected value for Mic Button clicks:"+mic_button_clicks);
                            microphone_button.setText("Start");
                        }catch(Exception e) {
                            Log.e(TAG, "Tried to stop/reset unavailable MediaRecorder!");
                            e.printStackTrace();

                        }
                        mic_button_clicks = (mic_button_clicks + 1) % MAX_RECORD_BTN_CLICKS;
                        break;

                }

            }

        });

        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if(mic != null)
        {
            try{
                //destroy mic object here
                mic = null;
            }catch(Exception e)
            {
                Log.e(TAG, "Exception while releasing AudioRecorder object:"+  e);
                e.printStackTrace();
            }
        }

        Log.d(TAG, "AudioRecorder.onDestroyView method was called.");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) {
            Log.e(TAG, "Error: Permission not granted to record");
            System.exit(1);
        }

    }


}
