package com.example.vangogh;

import android.Manifest;
import android.app.Activity;
//import android.app.FragmentManager;
import android.content.pm.PackageManager;
//import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dqt.libs.chorddroid.components.ChordTextureView;
import com.dqt.libs.chorddroid.*;

public class MainActivity extends FragmentActivity {

    ChordFragment chord_fragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FragmentManager man = getSupportFragmentManager();
        FragmentTransaction transaction = man.beginTransaction();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chord_fragment = (ChordFragment) man.findFragmentById(R.id.chord_fragment) ;
//        fragment = (RecordFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
//            transaction.add(R.id.chord_fragment, chord_fragment);
//            transaction.commit();
//        ChordTextureView chord = (ChordTextureView) findViewById(R.id.chord_texture_view);
//        chord.drawChord("Am", 0);
    }

    private void requestPermissions()
    {
//        for(final String perm : PERMISSIONS)
//        {
//            int res = checkSelfPermission( perm);
//            if (res == PackageManager.PERMISSION_GRANTED)
//            {
//                //then we do not need to worry
//            }
//            else if(res== PackageManager.PERMISSION_DENIED)
//            {
//                //proceed to ask user...
////                RequestMultiplePermissions.createIntent(this, PERMISSIONS);
//                Log.i(TAG, " permission has NOT been granted. Requesting permission.");
//
//                // BEGIN_INCLUDE(camera_permission_request)
//                if (shouldShowRequestPermissionRationale(
//                        perm)) {
//                    // Provide an additional rationale to the user if the permission was not granted
//                    // and the user would benefit from additional context for the use of the permission.
//                    // For example if the user has previously denied the permission.
////                    Log.i(TAG,
//                            "Displaying camera permission rationale to provide additional context.");
////                    Snackbar.make(R.layout.activity_main, "We need the permissions please",
////                            Snackbar.LENGTH_INDEFINITE)
////                            .setAction("Danke Schoen", new View.OnClickListener() {
////                                @Override
////                                public void onClick(View view) {
////                                    requestPermissions(new String[]{perm},
////                                    requestPermissions(new String[]{perm},
////                                            REQUEST_READ_STORAGE);
////                                }
////                            })
////                            .show();
//            }
//
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[] , int grantResults[])
//    {
//        switch(requestCode)
//        {
//            case REQUEST_READ_STORAGE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                }
//                return ;
//            case REQUEST_WRITE_STORAGE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                }
//                return ;
//            case REQUEST_RECORD_AUDIO:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    return
//                }
//
//                return ;
//            default:
//                Log.d(TAG,"Unknown permission requested $requestCode");
//                return;
//        }
//    }


}

}