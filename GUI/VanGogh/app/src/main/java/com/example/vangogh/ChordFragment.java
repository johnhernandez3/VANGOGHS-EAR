package com.example.vangogh;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dqt.libs.chorddroid.helper.DrawHelper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import chords.ChordFactory;
import chords.ChordModel;
import chords.ChordValidator;

/**
 * Class for displaying the Chord Diagram View to the user.
 */
public class ChordFragment extends Fragment
{
    int fret_position = 0; // from 0 to 12

    private final String TAG = "CHORD FRAG";
    private final int DIAGRAM_WIDTH = 200;
    private final int DIAGRAM_HEIGHT = 200;
    public String currchord = "";
    private DatabaseView dbview;
    private AudioPlayer ap;

    private ChordModel chord;
    private ChordFactory chord_factory;
    private ChordValidator chord_validator;

    //GUI Components for binding the Frontend
    Button update_btn;
    View view;
    ImageView chord_view;
    EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Loads the base view XML file
        view = inflater.inflate(R.layout.chord_fragment, container , false);

        // Bind Java Objects to XML Layout Views
        editText = (EditText) view.findViewById(R.id.chord_input);
        update_btn = (Button) view.findViewById(R.id.update_chord);
        chord_view = (ImageView) view.findViewById(R.id.chord_view);

        final FragmentManager man = this.getActivity().getSupportFragmentManager();


        // Set callback listener for events on the update button
        update_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                if(validateChord(editText.getText().toString())) {
                    Log.d(TAG, "editText received: " + editText.getText().toString());
                    currchord = editText.getText().toString();
                    drawChords("dm");
                    dbview = new DatabaseView();
                    ap = new AudioPlayer(Uri.fromFile(dbview.getChordsmap().get("dm")));
                    Log.d(TAG, "audioplayer received: " + Uri.fromFile(dbview.getChordsmap().get("dm")));
                    man.beginTransaction().add(R.id.new_audio_fragment_container_view, ap, "AUDIO PLAYER");


                }
                editText.setText("");
            }

        });

        return view;
    }

    /**
     * Verifies if the provided @param input_chord is a valid representation of a chord.
     * @param input_chord chord to be validated
     * @return boolean representing if the @param input_chord is valid.
     */
    private boolean validateChord(String input_chord)
    {
        chord_factory = new ChordFactory();
        ArrayList<ChordModel> valid_chords = chord_factory.createChords();

        chord_validator = new ChordValidator(valid_chords);

        if(chord_validator.isValidChord(input_chord))
            return true;


        return false;
    }


    /**
     * Draws to the Chord Fragment View the chord given by @param chordName
     * @param chordName String representation of the chord to be drawn
     * @param width int representation of diagram width
     * @param height int representation of diagram height
     */
    private void drawChords(String chordName, int width, int height)
    {
        Log.d(TAG, "Processing chord:"+chordName);
        if(validateChord(chordName)) {
            // Prepare data
            Resources resources = this.getResources();
            int transpose = 0; // transpose distance (-12 to 12)

            // Draw chord
            try {
                BitmapDrawable chord = DrawHelper.getBitmapDrawable(
                        resources, width, height, chordName, fret_position, transpose);

                // Display chord to image view
                this.chord_view.setImageDrawable(chord);
            }catch(Exception e)
            {
                e.printStackTrace();
                Log.e(TAG,"Error drawing chord:"+chordName);
                Toast.makeText(getActivity(), "Sorry, invalid chord provided!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getActivity(), "Sorry, invalid chord provided!", Toast.LENGTH_SHORT).show();
        }
    }

    /*
        Draws the Chord Diagram onto the View for the user.
     */
    private void drawChords(String chordName)
    {
        this.drawChords(chordName, DIAGRAM_WIDTH, DIAGRAM_HEIGHT);
    }

}
