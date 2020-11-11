package com.example.vangogh;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
<<<<<<< HEAD
<<<<<<< HEAD
import java.util.List;

import io_devices.IODeviceManager;
import utils.Controller;
import utils.Device;

/**
 * Class designated with the management of File logic in the system.
 * Queries for file URI's on the device, creates new files and deletes them.
 */
public class FileManager extends Activity implements IODeviceManager {
=======

public class FileManager extends Activity {
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======

public class FileManager extends Activity {
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
    private static final int FILE_SELECTED_CODE = 0;
    private static final String TAG = "FILE MANAGER";
    private static final int REQUEST_CHOOSER = 1234;

    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    private FileArrayAdapter files_adapter;
    private ArrayList<String> files = new ArrayList<>();

<<<<<<< HEAD
<<<<<<< HEAD
    private Uri selected_file;

=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
    private void testFilenames()
    {
        files.add("A");
        files.add("AB");
        files.add("B");
        files.add("O");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_manager);

        testFilenames();
        files_adapter = new FileArrayAdapter(this, files);
        ListView list_view = (ListView) findViewById(R.id.files_list) ;
        list_view.setAdapter(files_adapter);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {
            //If not ask the user for the permission
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
        else {
            Log.e(TAG, "PERMISSION GRANTED");

            try {
//                setupMediaRecorder(this.OutputFilePath());
//                recorder.prepare();
            } catch(Exception e)
            {
                Log.e(TAG, "Error preparing FileManager:" + e);
                e.printStackTrace();
            }
        }

        if (checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            //If not ask the user for the permission
             requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        else {
<<<<<<< HEAD
<<<<<<< HEAD
            Log.d(TAG, "PERMISSION GRANTED");
=======
            Log.e(TAG, "PERMISSION GRANTED");
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======
            Log.e(TAG, "PERMISSION GRANTED");
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec

            try {
//                setupMediaRecorder(this.OutputFilePath());
//                recorder.prepare();
            } catch(Exception e)
            {
                Log.e(TAG, "Error preparing FileManager:" + e);
                e.printStackTrace();
            }
        }
<<<<<<< HEAD
<<<<<<< HEAD

=======
=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
//        Create the ACTION_GET_CONTENT Intent
//        Intent getContentIntent = FileUtils.createGetContentIntent();

//        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
//        startActivityForResult(intent, REQUEST_CHOOSER);
<<<<<<< HEAD
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, REQUEST_CHOOSER);
    }

<<<<<<< HEAD
<<<<<<< HEAD
        public Uri returnChosenFile()
        {
            return this.selected_file;
        }

=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec

    private void showFilePicker()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try{
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECTED_CODE
            );
        } catch(android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(this, "No File Manager found, please install one!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode)
        {
            case FILE_SELECTED_CODE:

//                    Uri uri = data.getData();
//                    Log.d(TAG, "File URI:" +  uri.toString());
//
//                    String path = uri.getPath();
//                    Log.d(TAG, "File Path: "+ path);
//                    //process the file or pass it to data
////                    super.onActivityResult(requestCode, resultCode,data);

                break;

            case REQUEST_CHOOSER:

                Uri uri = data.getData();
                Log.d(TAG, "File URI:" +  uri.toString());
<<<<<<< HEAD
<<<<<<< HEAD
                selected_file=uri;
=======

>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======

>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
                String path = uri.getPath();
                Log.d(TAG, "File Path: "+ path);
                //process the file or pass it to data
                files.add(uri.toString());
<<<<<<< HEAD
<<<<<<< HEAD
                Intent files_d = new Intent();
                files_d.putExtra("file", uri.toString());
                setResult(Activity.RESULT_OK, files_d);
                finish();

                super.onActivityResult(REQUEST_CHOOSER, resultCode,files_d);
=======
                    super.onActivityResult(requestCode, resultCode,data);
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======
                    super.onActivityResult(requestCode, resultCode,data);
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec

                break;

        }

//        super.onActivityResult(requestCode, resultCode,data);
    }


<<<<<<< HEAD
<<<<<<< HEAD
    @Override
    public List<Device> devices() {
        return null;
    }

    @Override
    public boolean addDevice(Device device) throws IllegalArgumentException {
        return false;
    }

    @Override
    public boolean removeDevice(Device device) throws IllegalArgumentException {
        return false;
    }

    @Override
    public Controller getDevice(Device device) throws IllegalArgumentException {
        return null;
    }

    @Override
    public boolean addController(Controller control) {
        return false;
    }

    @Override
    public boolean isValid(Controller control) {
        return false;
    }
=======


>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======


>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
}
