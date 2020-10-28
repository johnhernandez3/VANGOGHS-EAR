package com.example.vangogh;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import chords.Tablature;

public class TablatureFragment extends Fragment
{
    final static String TAG = "TABLATURE";
    Tablature tab;
    View view;
    Button load_btn;
   TextView tablature_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Loads the base view XML file
        view = inflater.inflate(R.layout.tablature_fragment, container , false);

        // Bind Java Objects to XML Layout Views
//        editText = (EditText) view.findViewById(R.id.chord_input);
        tablature_text = (TextView) view.findViewById(R.id.tablature_text);
        load_btn = (Button) view.findViewById(R.id.load_tablature);
//        chord_view = (ImageView) view.findViewById(R.id.chord_view);

        // Set callback listener for events on the update button
        load_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                setEmptyTablature();
            }

        });

        return view;
    }

    private void setEmptyTablature()
    {
        tab = new Tablature();//create empty tablature
        Log.d(TAG, "Tablature:"+tab.toString());
        tablature_text.setText(tab.toString());//set the text view as the empty tablature
    }


}
