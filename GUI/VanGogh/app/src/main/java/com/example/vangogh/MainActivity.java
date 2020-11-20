package com.example.vangogh;

import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    Button dbview_button;
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

    /**
     * When the object is created, it finds the Main View for the instance life cycle
     * @param savedInstanceState the state of the parent that called the object if it wants to know anything
     */
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

                Log.d(TAG, "Entering On Checked Changed Listener");
                if(isChecked)
                {
                    if(man.findFragmentByTag("AUDIO PLAYER")!=null) {
                        //remove this one
                        man.beginTransaction().remove(man.findFragmentByTag("AUDIO PLAYER")).commit();
                    }



                    Log.d(TAG, "Entered On Checked Flag");
                    //TODO: Fix Bug here with the permissions, refuses to ask or verify that they were granted
                    // Fails to add audio recorder frag as a consequence
//                    if(checkSelfPermission("RECORD AUDIO") == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Adding Audio Recorder Fragment");
                        audio_fragment = new AudioRecorder();
                        man.beginTransaction().add(R.id.audio_fragment_container_view, audio_fragment, "AUDIO RECORD FRAG").commit();
//                    }else{
//                        // Ask for record permissions here
//                        requestPermissions( new String[]{"RECORD AUDIO"}, REQUEST_RECORD_AUDIO);
//                    }

//                    if(man.findFragmentByTag("AUDIO RECORD FRAG")!=null) {
//                        //remove this one
//                        man.beginTransaction().remove(man.findFragmentByTag("AUDIO RECORD FRAG")).commit();
//                    }
                }
                else{
                    Log.d(TAG, "Failed On Checked Flag");

                    if(man.findFragmentByTag("AUDIO PLAYER")!=null) {
                        //remove this one
                        man.beginTransaction().remove(man.findFragmentByTag("AUDIO PLAYER")).commit();
                    }

//
                    if(man.findFragmentByTag("AUDIO RECORD FRAG")!=null) {
                        //remove this one
                        man.beginTransaction().remove(man.findFragmentByTag("AUDIO RECORD FRAG")).commit();
                    }

                    if(man.findFragmentByTag("TABLATURE")!=null) {
                        //remove this one
                        man.beginTransaction().remove(man.findFragmentByTag("TABLATURE")).commit();
                    }

                    if(man.findFragmentByTag("CHORD FRAG")!=null) {
                        //remove this one
                        man.beginTransaction().remove(man.findFragmentByTag("CHORD_FRAG")).commit();
                    }

                }

            }
        });


        toggle_frags = (ToggleButton) findViewById(R.id.toggle_frags);

        toggle_frags.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                Log.d(TAG, "Entering On Checked Changed Listener");
                if(isChecked)
                {
                    if(man.findFragmentByTag("AUDIO RECORD FRAG")!=null) {
                        //remove this one
                        man.beginTransaction().remove(man.findFragmentByTag("AUDIO RECORD FRAG")).commit();
                    }

                    if(man.findFragmentByTag("AUDIO PLAYER")!=null) {
                        //remove this one
                        man.beginTransaction().remove(man.findFragmentByTag("AUDIO PLAYER")).commit();
                    }



                    Log.d(TAG, "Entered On Checked Flag");
                    if(man.findFragmentByTag("TABLATURE")!=null)
                    {
                        //remove this one
                        man.beginTransaction().remove(man.findFragmentByTag("TABLATURE")).commit();
                    }
                    chord_fragment = (ChordFragment) new ChordFragment();
                    man.beginTransaction().add(R.id.fragment_container_view, chord_fragment, "CHORD FRAG").commit();

                }
                else{
                    Log.d(TAG, "Failed On Checked Flag");
                    tablature_fragment = (TablatureFragment) new TablatureFragment();
                    if(man.findFragmentByTag("CHORD FRAG")!=null)
                    {
                        //remove this one
                        man.beginTransaction().remove(man.findFragmentByTag("CHORD FRAG")).commit();
                    }

                    man.beginTransaction().add(R.id.fragment_container_view, tablature_fragment, "TABLATURE").commit();

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


        final Intent intent = new Intent(this, DatabaseView.class);
        dbview_button = (Button) findViewById(R.id.dbview_button);
        dbview_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(intent);
            }
        });


        String a = "a.wav";
        String fileContents = "Hello world!";
        try (FileOutputStream fos = this.openFileOutput(a, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String am = "am.wav";
        String fileContents1 = "Hello world!1";
        try (FileOutputStream fos = this.openFileOutput(am, Context.MODE_PRIVATE)) {
            fos.write(fileContents1.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String bm = "bm.wav";
        String fileContents2 = "Hello world!2";
        try (FileOutputStream fos = this.openFileOutput(bm, Context.MODE_PRIVATE)) {
            fos.write(fileContents2.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String c = "c.wav";
        String fileContents3 = "Hello world!3";
        try (FileOutputStream fos = this.openFileOutput(c, Context.MODE_PRIVATE)) {
            fos.write(fileContents3.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String d = "d.wav";
        String fileContents4 = "Hello world!4";
        try (FileOutputStream fos = this.openFileOutput(d, Context.MODE_PRIVATE)) {
            fos.write(fileContents4.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String dm = "dm.wav";
        String fileContents5 = "Hello world!5";
        try (FileOutputStream fos = this.openFileOutput(dm, Context.MODE_PRIVATE)) {
            fos.write(fileContents5.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String echord = "e.wav";
        String fileContents6 = "Hello world!6";
        try (FileOutputStream fos = this.openFileOutput(echord, Context.MODE_PRIVATE)) {
            fos.write(fileContents6.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String em = "em.wav";
        String fileContents7 = "Hello world!7";
        try (FileOutputStream fos = this.openFileOutput(em, Context.MODE_PRIVATE)) {
            fos.write(fileContents7.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String f = "f.wav";
        String fileContents8 = "Hello world!8";
        try (FileOutputStream fos = this.openFileOutput(f, Context.MODE_PRIVATE)) {
            fos.write(fileContents8.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String g = "g.wav";
        String fileContents9 = "Hello world!9";
        try (FileOutputStream fos = this.openFileOutput(g, Context.MODE_PRIVATE)) {
            fos.write(fileContents9.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }





    }

//    /**
//     *Calls the FileManager class to move the files
//     */
//    public void searchForFile()
//    {
//        Intent intent = new Intent(this, FileManager.class);
//
//        startActivity(intent);
//
//
//    }

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
     * Asks for the permission that is missing, if it is denied it asks the user for permission
     */

    /**
     * When activity requires a permission
     * @param requestCode an int that identifies the type of permission that is being asked
     * @param permissions string array of the permissions being asked
     * @param grantResults int array with numbers if it has a 0, it is denied the result, else anything different,
     * it is granted
     */
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
//                boolean bool = data.getBooleanExtra("bool");
                FragmentManager man = this.getSupportFragmentManager();
                AudioPlayer audio_player = new AudioPlayer(selected_recording);

                man.beginTransaction().add(R.id.fragment_container_view, audio_player, "AUDIO PLAYER").commit();


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }



}

