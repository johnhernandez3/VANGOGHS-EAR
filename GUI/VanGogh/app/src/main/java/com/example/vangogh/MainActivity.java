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

/**
 * Class 
 */
public class MainActivity extends FragmentActivity {

    AudioRecorder audio_fragment;
    ChordFragment chord_fragment;
    private View view;
    private final String TAG = "MAIN";
    private final int REQUEST_READ_STORAGE = 0;
    private final int REQUEST_WRITE_STORAGE = 1;
    private final int REQUEST_RECORD_AUDIO = 2;
    private static final String[] PERMISSIONS = {
            "RECORD_AUDIO",
            "READ_EXTERNAL_STORAGE",
            "WRITE_EXTERNAL_STORAGE",
            "ACCESS_MEDIA_LOCATION"
    };

    
    /**
     * When the object is created, it finds the Main View for the instance life cycle
     * @param savedInstanceState the state of the parent that called the object if it wants to know anything
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        FragmentManager man = getSupportFragmentManager();
        FragmentTransaction transaction = man.beginTransaction();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        if(checkSelfPermission(PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED) {
            audio_fragment = (AudioRecorder) man.findFragmentById(R.id.audio_fragment);
        }else{
            // Ask for permissions here
            requestPermissions();
        }
        chord_fragment = (ChordFragment) man.findFragmentById(R.id.chord_fragment) ;
        Button find_file = (Button) findViewById(R.id.find_button);
        find_file.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                searchForFile();
            }

        });

//        fragment = (RecordFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
//            transaction.add(R.id.chord_fragment, chord_fragment);
//            transaction.commit();
//        ChordTextureView chord = (ChordTextureView) findViewById(R.id.chord_texture_view);
//        chord.drawChord("Am", 0);
    }

    /**
     *Calls the FileManager class to move the files
     */
    public void searchForFile()
    {
        Intent intent = new Intent(this, FileManager.class);

        startActivity(intent);

    }

    /**
     * Asks for the permission that is missing, if it is denied it asks the user for permission
     */
    private void requestPermissions()
    {
        for(final String perm : PERMISSIONS)
        {
            int res = checkSelfPermission( perm);
            if (res == PackageManager.PERMISSION_GRANTED)
            {
                //then we do not need to worry
                Log.d(TAG, "Permission:["+perm+"]\n already granted!");
            }
            else if(res== PackageManager.PERMISSION_DENIED)
            {
                //proceed to ask user...
                // RequestMultiplePermissions.createIntent(this, PERMISSIONS);
                Log.i(TAG, "Permission:["+perm+"] has NOT been granted.\n Requesting permission.");

                // BEGIN_INCLUDE(camera_permission_request)
//                if (shouldShowRequestPermissionRationale(perm)) {
//                    // Provide an additional rationale to the user if the permission was not granted
//                    // and the user would benefit from additional context for the use of the permission.
//                    // For example if the user has previously denied the permission.
//                    Log.i(TAG, "Displaying camera permission rationale to provide additional context.");
//                    Snackbar.make(view, "We need the permissions please", Snackbar.LENGTH_INDEFINITE)
//                            .setAction("Danke Schoen", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    requestPermissions(new String[]{perm},
////                                    requestPermissions(new String[]{perm},
//                                            REQUEST_READ_STORAGE);
//                                }
//                            })
//                            .show();
//
//                }
                requestPermissions( new String[]{perm}, REQUEST_READ_STORAGE);
            }

        }
    }

    /**
     * When activity requires a permission
     * @param requestCode an int that identifies the type of permission that is being asked
     * @param permissions string array of the permissions being asked
     * @param grantresults int array with numbers if it has a 0, it is denied the result, else anything different, 
     * it is granted
     */
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
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Granted Write Storage Permission");
                }
                return ;
            case REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Granted Record Audio Permission");
                    return;
                }

                return ;
            default:
                Log.d(TAG,"Unknown permission requested:["+requestCode+"]\n");
                return;
        }
    }


}

