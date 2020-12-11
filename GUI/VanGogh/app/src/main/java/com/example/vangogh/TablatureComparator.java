package com.example.vangogh;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import chords.ChordToTab;
import io_devices.Microphone;

public class TablatureComparator extends AppCompatActivity
{
    Microphone mic;
    private final String TAG = "COMPARATOR";
    Uri selected_recording;
    String selected_tablature;
    private ArrayList<String> tab_1_chords, tab_2_chords;
    TablatureFragment a_tablature_fragment, b_tablature_fragment;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    private void removeAllFragments()
    {
        FragmentManager man = this.getSupportFragmentManager();

        for(Fragment frag : man.getFragments())
        {
            if(frag != null)
                man.beginTransaction().remove(frag).commit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //Receives the URI of selected file from FileManager class
        if (requestCode == 1234) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("file");
                Uri uri = Uri.parse(result);
                selected_recording = uri;
                Log.d(TAG, "Saved URI of selected recording:"+uri);
                FragmentManager man = this.getSupportFragmentManager();
                AudioPlayer audio_player = new AudioPlayer(selected_recording);

                removeAllFragments();

                man.beginTransaction().add(R.id.fragment_container_view, audio_player, "AUDIO PLAYER").commit();


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // there's no result
            }
        }

        if (requestCode == 5678) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("file");
                Log.d(TAG, "Received Intent URI:"+ result);
                Uri uri = Uri.parse(result);
                selected_tablature = result;

                Log.d(TAG, "Saved PATH of selected recording:"+result);

                FragmentManager man = this.getSupportFragmentManager();
                FileManager fm = new FileManager(this);
                try {
                    ArrayList<String> predicted_chords =
                            fm.readFromLabelsFile(uri);
//                String predicted_tablature = ChordToTab.totalTablature(ChordToTab.constructTab(predicted_chords.toArray(new String[predicted_chords.size()])));
                    String predicted_tablature = ChordToTab.convertStringChords(predicted_chords);
                    TablatureFragment tab_frag = new TablatureFragment(predicted_tablature);

                    removeAllFragments();

                    man.beginTransaction().add(R.id.fragment_container_view, tab_frag, "TABLATURE").commit();
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
