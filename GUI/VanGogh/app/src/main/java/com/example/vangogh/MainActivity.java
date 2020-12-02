package com.example.vangogh;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.*;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.google.android.material.navigation.NavigationView;

import org.apache.commons.math3.exception.util.ExceptionContextProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Class for the Main View of the system and where the user will mainly interact
 */
public class MainActivity extends AppCompatActivity implements AppBarConfiguration.OnNavigateUpListener
{

    Map<String, Integer> permissions;
    TablatureFragment tablature_fragment;
    AudioRecorder audio_fragment;
    ChordFragment chord_fragment;
    FileManager fm;
    ToggleButton toggle_frags ;
    Button dbview_button;
    Toolbar toolbar;
    private View view;
    private AppBarConfiguration appBarConfiguration;
    private Uri selected_recording;

    private final String TAG = "MAIN";
    private final int REQUEST_READ_STORAGE = 0;
    private final int REQUEST_WRITE_STORAGE = 1;
    private final int REQUEST_RECORD_AUDIO = 2;
    private final int REQUEST_ACCESS_MEDIA = 3;
    private final int ALL_REQ_PERMS = 2020;
    private int clicks = 0;
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

        toolbar = (Toolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do stuff here when clicking the menu button
                if(clicks % 2 == 0)
                    findViewById(R.id.fragment_layout).setVisibility(View.INVISIBLE);
                else{
                    findViewById(R.id.fragment_layout).setVisibility(View.VISIBLE);
                }

                clicks = clicks + 1 % 2;
            }
        });

        requestPermissions();

        ToggleButton record_show_btn = (ToggleButton) findViewById(R.id.record_show_button);

        record_show_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                removeAllFragments();

                Log.d(TAG, "Entering On Checked Changed Listener");
                if(isChecked)
                {
//                    if(man.findFragmentByTag("AUDIO PLAYER")!=null) {
//                        //remove this one
//                        man.beginTransaction().remove(man.findFragmentByTag("AUDIO PLAYER")).commit();
//                    }



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

                removeAllFragments();

                Log.d(TAG, "Entering On Checked Changed Listener");
                if(isChecked)
                {
                    Log.d(TAG, "Entered On Checked Flag");
                    if(man.findFragmentByTag("CHORD") != null) {
                        Log.d(TAG, "Swapped TAB with CHORD");
                        swapFragments("TABLATURE", "CHORD");
                    }
                    else
                    {
                        FragmentManager manager = getSupportFragmentManager();

                        // no fragment, lets add it manually
                        manager.beginTransaction().add(R.id.fragment_container_view, new TablatureFragment(),"TABLATURE"  ).commit();

                    }
                }
                else{
                    Log.d(TAG, "Failed On Checked Flag");
                    if(man.findFragmentByTag("TABLATURE") != null) {

                        Log.d(TAG, "Swapped TAB with CHORD");
                        swapFragments("CHORD", "TABLATURE");
                    }
                    else
                    {
                        FragmentFactory fragmentFactory = new FragmentFactory();
                        FragmentManager manager = getSupportFragmentManager();
                        // no fragment, lets add it manually
                        manager.beginTransaction().add(R.id.fragment_container_view, new ChordFragment(), "CHORD").commit();
                    }
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


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_db_view:

                try{
                    final Intent intent = new Intent(this, DatabaseView.class);
                    startActivity(intent);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                Toast.makeText(this, "DB View selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.action_record_view:
                Toast.makeText(this, "Record View selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

//        return NavigationUI.onNavDestinationSelected(item, navController)
//                || super.onOptionsItemSelected(item);

        return true;
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
        Fragment incoming_fragment = FragmentFactory.createFragment(incoming);
        if(incoming.isEmpty() && outgoing.isEmpty())
        {
            // Do Nothing
            return;
        }

        if(outgoing.isEmpty())
        {
                man.beginTransaction().add(R.id.fragment_container_view,incoming_fragment, incoming).commit();
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


        if(incoming_fragment != null)
        {
            man.beginTransaction().add(R.id.fragment_container_view,incoming_fragment, incoming).commit();
        }

        else{
            Log.e(TAG, "Invalid fragment created for swapping views:" + incoming);
            throw new IllegalStateException("Invalid fragment created for swapping views:"+ incoming);
        }

        return;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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

