package com.example.vangogh;

import android.Manifest;
import android.app.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

import chords.ChordFactory;

/**
 * Class for the Main View of the system and where the user will mainly interact
 */
public class MainActivity extends FragmentActivity
{

    Map<String, Integer> permissions;
    TablatureFragment tablature_fragment;
    AudioRecorder audio_fragment;
    ChordFragment chord_fragment;
    ToggleButton toggle_frags ;
    private View view;

    private Uri selected_recording;

    private final String TAG = "MAIN";
    private final int REQUEST_READ_STORAGE = 0;
    private final int REQUEST_WRITE_STORAGE = 1;
    private final int REQUEST_RECORD_AUDIO = 2;
    private final int REQUEST_ACCESS_MEDIA = 3;
    private final int ALL_REQ_PERMS = 2020;

    private static final String[] PERMISSIONS = {
            "RECORD_AUDIO",
            "READ_EXTERNAL_STORAGE",
            "WRITE_EXTERNAL_STORAGE",
            "ACCESS_MEDIA_LOCATION"
    };

    private void preparePermissions()
    {
        int  i = 0;
        permissions = new HashMap<>();
        for(String permission : PERMISSIONS)
        {
            permissions.put(permission ,i);
            i+=1;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        preparePermissions();

        final FragmentManager man = getSupportFragmentManager();
        final FragmentTransaction transaction = man.beginTransaction();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        ToggleButton record_show_btn = (ToggleButton) findViewById(R.id.record_show_button);

        record_show_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                removeAllFragments();

                Log.d(TAG, "Entering On Checked Changed Listener");
                if(isChecked)
                {

                    Log.d(TAG, "Entered On Checked Flag");
                    //TODO: Fix Bug here with the permissions, refuses to ask or verify that they were granted
                    // Fails to add audio recorder frag as a consequence
//                    if(checkSelfPermission("RECORD AUDIO") == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Adding Audio Recorder Fragment");
                        audio_fragment = new AudioRecorder();
                        man.beginTransaction().add(R.id.audio_fragment_container_view, audio_fragment, "AUDIO RECORDER").commit();
//                    }else{
//                        // Ask for record permissions here
//                        requestPermissions( new String[]{"RECORD AUDIO"}, REQUEST_RECORD_AUDIO);
//                    }
                }
                else{
                    Log.d(TAG, "Failed On Checked Flag");

                    swapFragments("", "AUDIO RECORDER");

                }

            }
        });


        toggle_frags = (ToggleButton) findViewById(R.id.toggle_frags);

        toggle_frags.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                removeAllFragments();

                Log.d(TAG, "Entering On Checked Changed Listener");
                if(isChecked)
                {
                    Log.d(TAG, "Entered On Checked Flag");

                    swapFragments("TABLATURE", "CHORD");
                }
                else{
                    Log.d(TAG, "Failed On Checked Flag");

                    swapFragments("CHORD", "TABLATURE");

                }

            }
        });

        Button find_file_btn = (Button) findViewById(R.id.find_button);
        find_file_btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                searchForFile();
            }

        });




    }

    private void removeAllFragments()
    {
        FragmentManager man = this.getSupportFragmentManager();

        for(Fragment frag : man.getFragments())
        {
            if(frag != null)
                man.beginTransaction().remove(frag).commit();
        }

    }


    private void swapFragments(String incoming, String outgoing)
    {
        FragmentManager man = this.getSupportFragmentManager();

        if(incoming.isEmpty() && outgoing.isEmpty() || outgoing.isEmpty())
        {
            // Do Nothing
            return;
        }

        if(incoming.isEmpty())
        {
            man.beginTransaction().remove(man.findFragmentByTag(outgoing)).commit();
            return ;
        }

        if(man.findFragmentByTag(outgoing)!=null)
        {
            //remove this one
            man.beginTransaction().remove(man.findFragmentByTag(outgoing)).commit();
        }
        Fragment incoming_fragment = FragmentFactory.createFragment(incoming);
        if(incoming_fragment != null)
        {
            man.beginTransaction().add(R.id.fragment_container_view,incoming_fragment, incoming).commit();
        }

        else{
            Log.e(TAG, "Invalid fragment created for swapping views:" + incoming);
            throw new IllegalStateException("Invalid fragment created for swapping views:"+ incoming);
        }

    }

    /**
     * Generates an intent for the FileManager activity and awaits a result with code 1234 for a file URI.
     */
    public void searchForFile()
    {
        // Asks FileManager to be initialized and awaits the result of selected file
        Intent intent = new Intent(this, FileManager.class);

        startActivityForResult(intent,1234);

    }

    /**
     * Asks the user for IO device permissions such as accessing storage and the microphone
     */
    private void requestPermissions()
    {
        requestPermissions((String[])permissions.keySet().toArray(new String[permissions.keySet().size()]),ALL_REQ_PERMS);
    }

    //TODO: Fix Permissions Request for File Storage and Audio Recording
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[] , int grantResults[])
    {
        switch(requestCode)
        {
            case REQUEST_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Granted Read Storage Permission");
                }
                return ;

            case REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Granted Write Storage Permission");
                }
                return ;

            case REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Granted Record Audio Permission");
                    return;
                }

                return ;

            case REQUEST_ACCESS_MEDIA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Granted Access Media Permission");
                    return;
                }

                return ;

            case ALL_REQ_PERMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Granted All Permissions");
                    return;
                }
                return ;

            default:
                Log.d(TAG,"Unknown permission requested:["+requestCode+"]\n");
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Receives the URI of selected file from FileManager class
        if (requestCode == 1234) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("file");
                Uri uri = Uri.parse(result);
                selected_recording = uri;
                Log.d(TAG, "Saved URI of selected recording:"+uri);
                FragmentManager man = this.getSupportFragmentManager();
                AudioPlayer audio_player = new AudioPlayer(selected_recording);

                removeAllFragments();

                man.beginTransaction().add(R.id.fragment_container_view, audio_player, "AUDIO PLAYER").commit();


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }



}

