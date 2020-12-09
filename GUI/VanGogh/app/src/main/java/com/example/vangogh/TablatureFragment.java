package com.example.vangogh;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import chords.ChordModel;
import chords.Tablature;

public class TablatureFragment extends Fragment
{
    private ArrayList<ChordModel> chords;
    final static String TAG = "TABLATURE";
    Tablature tab;
    View view;
    Button load_btn;
    TextView tablature_text;
    boolean receivedTab = false;
    private String saved_tab;

    public TablatureFragment(String tablature)
    {
        this(new ArrayList<>());
        receivedTab = true;
        this.saved_tab = tablature;

    }



    public TablatureFragment(ArrayList<ChordModel> chords)
    {
        if(chords != null)
            this.chords = chords;
        else{
            this.chords = new ArrayList<>();
        }
    }

    public TablatureFragment()
    {
        this.chords = new ArrayList<>();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Loads the base view XML file
        view = inflater.inflate(R.layout.tablature_fragment, container , false);

        // Bind Java Objects to XML Layout Views
        tablature_text = (TextView) view.findViewById(R.id.tablature_text);
        tablature_text.setMovementMethod(new ScrollingMovementMethod());
        load_btn = (Button) view.findViewById(R.id.load_tablature);


        // Set callback listener for events on the update button
        load_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(chords.isEmpty() &&!receivedTab)
                    setEmptyTablature();
                else{
                    setSavedTablature();
                }
            }

        });

        return view;
    }

    private void loadTablature()
    {

    }


    private void setEmptyTablature()
    {
        tab = new Tablature();//create empty tablature
        Log.d(TAG, "Tablature:"+tab.toString());
        tablature_text.setText(tab.toString());//set the text view as the empty tablature
    }

    private void setSavedTablature()
    {
        if(this.receivedTab)
        {
            tablature_text.setText(this.saved_tab);
        }
        else {
            tab = new Tablature(this.chords);
            tablature_text.setText(tab.toString());
        }
    }





}
