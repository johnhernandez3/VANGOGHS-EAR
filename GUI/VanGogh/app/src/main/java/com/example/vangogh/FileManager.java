package com.example.vangogh;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.database.MusicDataBase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io_devices.IODeviceManager;
import utils.Controller;
import utils.Device;

/**
 * Class designated with the management of File logic in the system.
 * Queries for file URI's on the device, creates new files and deletes them.
 */
public class FileManager extends Activity implements IODeviceManager
{
    private String CHORD_DIR = "chords";
    private static final int FILE_SELECTED_CODE = 0;
    private static final String TAG = "FILE MANAGER";
    private static final int REQUEST_CHOOSER = 1234;

    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    private FileArrayAdapter files_adapter;
    private ArrayList<String> files = new ArrayList<>();

    private Uri selected_file;
//    Context context;

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
            res = this.getExternalFilesDir(null).getAbsolutePath() + "/" + filename + "." +format;
        }
        else{
            res = this.getExternalFilesDir(null).getAbsolutePath() + "/" + "sample" + "."+format;
        }
        return res;
    }

    public String getAbsoluteChordsDirPath()
    {
        return getAbsoluteProjectPath() + '/' + CHORD_DIR;
    }


    public String getAbsoluteProjectPath()
    {
        String path_str = this.getExternalFilesDir(null).getAbsolutePath().toString();

        return path_str;
    }


    public Uri getAbsoluteProjectPathURI()
    {

        Uri path = Uri.parse(getAbsoluteProjectPath());

        return path;
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

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, REQUEST_CHOOSER);
        try {
            writeChordsToFilesDir();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void writeChordsToFilesDir() throws IOException {
        int chords[] = {R.raw.a, R.raw.am, R.raw.bm, R.raw.c, R.raw.d, R.raw.d, R.raw.dm, R.raw.e, R.raw.em, R.raw.f, R.raw.g};
//        for(int i = 0; i < chords.length; i++) {
            copyRAWtoPhone(chords[0], "/data/data/com.example.vangogh/files/a.wav");
            File findFile = new File("/data/data/com.example.vangogh/files/a.wav");
            if(findFile.exists()) Log.d(TAG, "writeChordsToFilesDir: File Found!");
//        }
    }

    private void copyRAWtoPhone(int id, String path) throws IOException {
        // Add a specific media item.
        ContentResolver resolver = getApplicationContext()
                .getContentResolver();

// Find all audio files on the primary external storage device.
        Uri audioCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            audioCollection = MediaStore.Audio.Media
                    .getContentUri(MediaStore.getVersion(this));
        } else {
            audioCollection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

// Publish a new song.
        ContentValues newSongDetails = new ContentValues();
        newSongDetails.put(MediaStore.Audio.Media.DISPLAY_NAME,
                "a.wav");



// Keeps a handle to the new song's URI in case we need to modify it
// later.
//        Uri myFavoriteSongUri = resolver
//                .insert(audioCollection, newSongDetails);
    }
}
