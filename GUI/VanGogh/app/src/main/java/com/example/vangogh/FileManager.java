package com.example.vangogh;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class FileManager extends Activity {
    private static final int FILE_SELECTED_CODE = 0;
    private static final String TAG = "FILE MANAGER ACTIVITY";
    private static final int REQUEST_CHOOSER = 1234;

    private FileArrayAdapter files_adapter;
    private ArrayList<String> files = new ArrayList<>();

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
        //Create the ACTION_GET_CONTENT Intent
//        Intent getContentIntent = FileUtils.createGetContentIntent();

//        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
//        startActivityForResult(intent, REQUEST_CHOOSER);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        startActivityForResult(intent, REQUEST_CHOOSER);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode)
        {
            case FILE_SELECTED_CODE:
                if( requestCode == RESULT_OK)
                {
                    Uri uri = data.getData();
                    Log.d(TAG, "File URI:" +  uri.toString());

                    String path = uri.getPath();
                    Log.d(TAG, "File Path: "+ path);
                    //process the file or pass it to data
//                    super.onActivityResult(requestCode, resultCode,data);
                }
                break;

        }

        super.onActivityResult(requestCode, resultCode,data);
    }




}
