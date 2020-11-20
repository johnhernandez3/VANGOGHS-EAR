package com.example.vangogh;

import android.app.Activity;
import android.app.AlertDialog;
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

public class DatabaseView extends Activity {

    //Create variable
    MusicDataBase myMusic;
    Button db_button;
    Button db_button2;
    Button view_button;
    Button del_button;
    Button view2_button;
    Button del2_button;
    EditText editSongName, editTextId, editTextChord, editTextId2;

    public HashMap<String, File> chordsmap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_layout);
        myMusic = new MusicDataBase(this);
        editTextId = (EditText) findViewById(R.id.editTextTextId);
        editTextId2 = (EditText) findViewById(R.id.editTextTextId2);
        editSongName = (EditText) findViewById(R.id.editTextTextSongName);
        editTextChord = (EditText) findViewById(R.id.editTextTextChord);


        db_button = (Button) findViewById(R.id.db_button);
        db_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInsert = myMusic.insertData(editSongName.getText().toString(), 1);
                if (isInsert) {
                    Toast.makeText(DatabaseView.this,"Data Inserted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(DatabaseView.this,"Data Not Inserted", Toast.LENGTH_LONG).show();
                }
            }
        });

        db_button2 = (Button) findViewById(R.id.db_button2);
        db_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInsert = myMusic.insertData(editTextChord.getText().toString(), 2);
                if (isInsert) {
                    Toast.makeText(DatabaseView.this,"Data Inserted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(DatabaseView.this,"Data Not Inserted", Toast.LENGTH_LONG).show();
                }
            }
        });

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
                    if(i%2 == 0) {
                        buff.append("Id: " + res.get(i));
                    }
                    else {
                        buff.append("\nSongName: " + res.get(i) + "\n\n");
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
                    if(i%2 == 0) {
                        buff.append("Id: " + res.get(i));
                    }
                    else {
                        buff.append("\nChords: " + res.get(i) + "\n\n");
                    }
                }

                showMessage("Chords", buff.toString());
            }
        });

        del_button = (Button) findViewById(R.id.del_button);
        del_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer delRows = myMusic.deleteData(editTextId.getText().toString());
                if(delRows > 0)  {
                    Toast.makeText(DatabaseView.this,"Data Deleted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(DatabaseView.this,"Data Not Deleted", Toast.LENGTH_LONG).show();
                }
            }
        });

        del2_button = (Button) findViewById(R.id.del_button2);
        del2_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer delRows = myMusic.deleteChordData(editTextId2.getText().toString());
                if(delRows > 0)  {
                    Toast.makeText(DatabaseView.this,"Data Deleted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(DatabaseView.this,"Data Not Deleted", Toast.LENGTH_LONG).show();
                }
            }
        });



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
    public HashMap<String, File> getChordsmap() {
        chordsmap.put("a", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "\\chords\\a.wav"));
        chordsmap.put("am", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "\\chords\\am.wav"));
        chordsmap.put("bm", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "\\chords\\bm.wav"));
        chordsmap.put("c", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "\\chords\\c.wav"));
        chordsmap.put("d", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "\\chords\\d.wav"));
        chordsmap.put("dm", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "\\chords\\dm.wav"));
        chordsmap.put("e", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "\\chords\\e.wav"));
        chordsmap.put("em", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "\\chords\\em.wav"));
        return this.chordsmap;
    }

}
