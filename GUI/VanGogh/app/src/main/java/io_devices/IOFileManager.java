package io_devices;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.database.MusicDataBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IOFileManager {
    private static final int FILE_SELECTED_CODE = 0;
    private static final String TAG = "IO FILE MANAGER";



    private Uri selected_file;

    private Context context;


    public IOFileManager(Context context)
    {
        this.context  = context;
    }



    /**
     * Allows the user to choose the files that they are seeing
     */
    public Uri returnChosenFile()
    {
        return this.selected_file;
    }


    public List getAllChords()
    {
        MusicDataBase music_database = new MusicDataBase(this.context);

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

            res = this.context.getExternalFilesDir(null).getAbsolutePath() + "/" + filename + "." +format;
        }
        else{
            res = this.context.getExternalFilesDir(null).getAbsolutePath() + "/" + "sample" + "."+format;
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


}
