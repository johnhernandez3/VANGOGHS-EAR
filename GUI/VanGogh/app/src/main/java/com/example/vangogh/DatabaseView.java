package com.example.vangogh;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.database.MusicDataBase;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io_devices.Microphone;

public class DatabaseView extends Activity {

    //Create variable
    MusicDataBase myMusic;
    Microphone microphone;
    Button view_button;
    Button del_button;
    Button view2_button;
    EditText editSongName;
    String[] chords = {"a", "am", "bm", "c", "d", "dm", "e", "em", "f", "g"};

    public HashMap<String, Integer> chordsmap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_layout);
        microphone = new Microphone();
        myMusic = new MusicDataBase(this);
        editSongName = (EditText) findViewById(R.id.editTextTextSongName);

        List<String> chord = myMusic.getAllID2();
        if(!(chord.size() > 0)) {
            boolean[] inserted = new boolean[10];
            for(int i = 0; i < inserted.length; i++) {
                inserted[i] = myMusic.insertData(chords[i], 2);
            }
        }

        view_button = (Button) findViewById(R.id.viewtablature_button);
        view_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> res = myMusic.getAllID();
                if(res.size() == 0) {
                    showMessage("Error", "Nothing found");
                    return;
                }

                StringBuffer buff = new StringBuffer();
                for(int i = 0; i < res.size(); i++) {
                    if(i%2 != 0) {
                        buff.append("\nTablature Name: " + res.get(i) + "\n\n");
                    }
                }
                showMessage("Tablatures", buff.toString());
            }
        });

        view2_button = (Button) findViewById(R.id.viewchord_button);
        view2_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> res = myMusic.getAllID2();
                if(res.size() == 0) {
                    showMessage("Error", "Nothing found");
                    return;
                }

                StringBuffer buff = new StringBuffer();
                for(int i = 0; i < res.size(); i++) {
                    if(i%2 != 0) {
                        buff.append(res.get(i) + "\n\n");
                    }
                }

                showMessage("Chords", buff.toString());
            }
        });

        del_button = (Button) findViewById(R.id.del_button);
        del_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Test this
                File filetoDelete = new File(microphone.getLabelsDirectoryFilePath() + "/" + editSongName.getText().toString() + ".wav");
                File tablaturetoDelete = new File(microphone.getLabelsFilePath() + "/" + editSongName.getText().toString() + ".txt");
                boolean deleted = filetoDelete.delete();
                boolean deleted2 = tablaturetoDelete.delete();
                Integer delRows = myMusic.deleteData(editSongName.getText().toString());
                if(delRows > 0 && deleted && deleted2)  {
                    Toast.makeText(DatabaseView.this,"Data Deleted", Toast.LENGTH_LONG).show();
                } else if(delRows > 0 && deleted && !deleted2) {
                    Toast.makeText(DatabaseView.this, "Tablature Not Deleted Please Erase " + editSongName.getText().toString() + ".txt", Toast.LENGTH_SHORT).show();
                } else if(delRows > 0 && !deleted && !deleted2) {
                    Toast.makeText(DatabaseView.this, "Only Rows Deleted Please Erase " + editSongName.getText().toString() + ".txt and " + editSongName.getText().toString() + ".wav", Toast.LENGTH_SHORT).show();
                } else if(delRows > 0 && !deleted && deleted2) {
                    Toast.makeText(DatabaseView.this, "Wav File Not Deleted Please Erase " + editSongName.getText().toString() + ".wav", Toast.LENGTH_SHORT).show();
                } else if(!(delRows > 0) && deleted && !deleted2) {
                    Toast.makeText(DatabaseView.this, "Data does not exist and Tablature Not Deleted Please Erase " + editSongName.getText().toString() + ".txt", Toast.LENGTH_SHORT).show();
                } else if(!(delRows > 0) && !deleted && deleted2) {
                    Toast.makeText(DatabaseView.this, "Data does not exist and Wav File Not Deleted Please Erase " + editSongName.getText().toString() + ".wav", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DatabaseView.this,"Data Not Deleted", Toast.LENGTH_LONG).show();
                }
            }
        });

        editSongName.setText("");


    }

    /**
     * Shows messages on a separate window
     * @param title the title of the message that is demonstrated on the window
     * @param msg the message demonstrated on the window
     */
    public void showMessage(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.show();
    }

    /**
     * Gets the chordsmap in the system
     */
    public HashMap<String, Integer> getChordsmap() {
        chordsmap.put("a", R.raw.a);
        chordsmap.put("am", R.raw.am);
        chordsmap.put("bm", R.raw.bm);
        chordsmap.put("c", R.raw.c);
        chordsmap.put("d", R.raw.d);
        chordsmap.put("dm", R.raw.dm);
        chordsmap.put("e", R.raw.e);
        chordsmap.put("em", R.raw.em);
        return this.chordsmap;
    }

}
