package com.example.vangogh;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
//import android.os.FileUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.database.MusicDataBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io_devices.IODeviceManager;
import utils.Controller;
import utils.Device;

/**
 * Class designated with the management of File logic in the system.
 * Queries for file URI's on the device, creates new files and deletes them.
 */
public class FileManager extends Activity implements IODeviceManager {
    private static final int FILE_SELECTED_CODE = 0;
    private static final String TAG = "FILE MANAGER";
    private static final int REQUEST_CHOOSER = 1234;

    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    private FileArrayAdapter files_adapter;
    private ArrayList<String> files = new ArrayList<>();

    private Uri selected_file;

    private Context context;

    public FileManager(Context context)
    {
        this.context  = context;
    }

    public FileManager()
    {
        this.context = this.getBaseContext();
    }


    private void testFilenames()
    {
        files.add("A");
        files.add("AB");
        files.add("B");
        files.add("O");
    }

    /**
     * When the object is created, it finds the File View for the instance life cycle
     * @param savedInstanceState the state of the parent that called the object if it wants to know anything
     */
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
            Log.d(TAG, "PERMISSION GRANTED");

            try {
//                setupMediaRecorder(this.OutputFilePath());
//                recorder.prepare();
            } catch(Exception e)
            {
                Log.e(TAG, "Error preparing FileManager:" + e);
                e.printStackTrace();
            }
        }
//        Create the ACTION_GET_CONTENT Intent
//        Intent getContentIntent = FileUtils.createGetContentIntent();

//        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
//        startActivityForResult(intent, REQUEST_CHOOSER);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, REQUEST_CHOOSER);
    }

    
    /**
     * Allows the user to choose the files that they are seeing
     */
    public Uri returnChosenFile()
    {
        return this.selected_file;
    }


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

    
    /**
     * When activity initiates a call for an action
     * @param requestCode an int that identifies the type of permission that is being asked
     * @param resultCode an int that identifies the result of the requestCode that was emitted
     * @param data data that is passed from one activity to the other if any
     */
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
                selected_file=uri;
                String path = uri.getPath();
                Log.d(TAG, "File Path: "+ path);
                //process the file or pass it to data
                files.add(uri.toString());
                Intent files_d = new Intent();
                files_d.putExtra("file", uri.toString());
                setResult(Activity.RESULT_OK, files_d);
                finish();

                super.onActivityResult(REQUEST_CHOOSER, resultCode,files_d);

                break;

        }

//        super.onActivityResult(requestCode, resultCode,data);
    }

    public List getAllChords()
    {
        MusicDataBase music_database = new MusicDataBase(this);

        return music_database.getAllID2();
    }


    /**
     * Generates the Output File Path for the Audio Recorder to store recorded audio.
     *
     * @param filename the name of the file to be created for storing the recorded audio.
     * @param format the file format that the data will be stored as.
     * @return String representation of the Output File Path with specified format
     */
    private String InputFilePath(String filename, String format)
    {
        String res;
        if(filename != null && filename != " ") {

            res = this.getExternalFilesDir(null).getAbsolutePath() + "/" + filename + "." +format;
        }
        else{
            res = this.getExternalFilesDir(null).getAbsolutePath() + "/" + "sample" + "."+format;
        }
        return res;
    }

    public Uri getChordsFilePathURI()
    {
        return Uri.fromFile(this.context.getFilesDir());
    }


    public File getChordFile(String chord_filename) throws Exception
    {
        Log.e(TAG, "Current stored context" + this.context.toString());
        Log.e(TAG, "Current stored context directory" + this.context.getFilesDir().getPath().toString());
        File a_file = new File(this.context.getFilesDir(), chord_filename);

        Log.d(TAG, "Created File:"+a_file.toString());
        if(a_file.exists())
            return a_file;
        else{
            throw new Exception("Error while trying to open file:"+a_file.getPath());
        }
    }

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
}
