package com.example.vangogh;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.dqt.libs.chorddroid.helper.DrawHelper;

public class ChordFragment extends Fragment {
    int fret_position = 0; // from 0 to 12
    private final String TAG = "CHORD FRAG";
    Button update_btn;
    View view;
    ImageView chord_view;
    EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.chord_fragment, container , false);

        editText = (EditText) view.findViewById(R.id.chord_input);
        update_btn = (Button) view.findViewById(R.id.update_chord);
        chord_view = (ImageView) view.findViewById(R.id.chord_view);

        update_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(),  TAG, Toast.LENGTH_SHORT).show();
                drawChords(editText.getText().toString());
            }

        });

        return view;
    }


    private void drawChords(String chordName) {

        /**
         * Set to ImageView
         */


        // Prepare data
        Resources resources = this.getResources();
        int width = 200;
        int height = 200;
//        String chordName = "Am";
        int transpose = 0; // transpose distance (-12 to 12)

        // Draw chord
        BitmapDrawable chord = DrawHelper.getBitmapDrawable(
                resources, width, height, chordName, fret_position, transpose);

        // Display chord to your image view
        chord_view.setImageDrawable(chord);

//        /**
//         * Use ChordTextureView
//         */
//
//        chordView.drawChord("C#", position);
//
//        /**
//         * Update view
//         */
//        txtHandPosition.setText(position + "");

    }

}
