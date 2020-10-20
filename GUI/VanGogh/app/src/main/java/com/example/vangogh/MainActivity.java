package com.example.vangogh;

import android.Manifest;
import android.app.Activity;
//import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dqt.libs.chorddroid.components.ChordTextureView;
import com.dqt.libs.chorddroid.*;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity
{

    Map<String, Integer> permissions;
    AudioRecorder audio_fragment;
    ChordFragment chord_fragment;

    private View view;

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

        FragmentManager man = getSupportFragmentManager();
        FragmentTransaction transaction = man.beginTransaction();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        if(checkSelfPermission(PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED) {
            audio_fragment = (AudioRecorder) man.findFragmentById(R.id.audio_fragment);
        }else{
            // Ask for record permissions here
            requestPermissions( new String[]{PERMISSIONS[0]}, REQUEST_RECORD_AUDIO);
        }

        chord_fragment = (ChordFragment) man.findFragmentById(R.id.chord_fragment) ;

        Button find_file_btn = (Button) findViewById(R.id.find_button);
        find_file_btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                searchForFile();
            }

        });

    }

    public void searchForFile()
    {
        
        Intent intent = new Intent(this, FileManager.class);

        startActivity(intent);

    }


    private void requestPermissions()
    {
//        for(Map.Entry<String, Integer> perm : this.permissions.entrySet())
////        {
////            int res = checkSelfPermission(perm.getKey());
////            if (res == PackageManager.PERMISSION_GRANTED)
////            {
////                //then we do not need to worry
////                Log.d(TAG, "Permission:["+perm.getKey()+"]\n already granted!");
////            }
////            else if(res== PackageManager.PERMISSION_DENIED)
////            {
////                //proceed to ask user...
////                // RequestMultiplePermissions.createIntent(this, PERMISSIONS);
////                Log.i(TAG, "Permission:["+perm.getKey()+"] has NOT been granted.\n Requesting permission.");
////
////                requestPermissions( new String[]{perm.getKey()}, perm.getValue());
////            }
////
////        }
        requestPermissions((String[])permissions.keySet().toArray(),ALL_REQ_PERMS);
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


}

