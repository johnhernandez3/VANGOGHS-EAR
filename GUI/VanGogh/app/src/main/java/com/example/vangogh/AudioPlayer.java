package com.example.vangogh;

import android.media.AudioAttributes;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import android.widget.SeekBar;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

/**
 * AudioPlayer for playing back chord audio from the user's selection
 */
public class AudioPlayer extends Fragment {

    // String used for debugging with LogCat utility
    private static  final String TAG = "AUDIO PLAYER";

    //GUI Elements for interacting with the audio file
    private Button play, stop,pause,forward,back;


    private View view;
    private MediaPlayer player;

    //The URI of the stored internal file to be played
    private Uri file;

    //A handler for the thread designated to update the seekbar
    private Handler handler;

    // SeekBar data for tracking Audio Playback progress
    private SeekBar seekbar;
    private static int one_time_only  = 0;
    private double start_time, final_time;

    public AudioPlayer(Uri file)
    {
        handler = new Handler();
        start_time = 0;
        final_time = 0;
        this.file = file;
    }


    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null. This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>A default View can be returned by calling {@link(int)} in your
     * constructor. Otherwise, this method returns null.
     *
     * <p>It is recommended to <strong>only</strong> inflate the layout in this method and move
     * logic that operates on the returned View to {@link #onViewCreated(View, Bundle)}.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Loads the base view XML file
        view = inflater.inflate(R.layout.audio_player_layout, container , false);

        // Bind Java Objects to XML Layout Views

        play = (Button) view.findViewById(R.id.play);
//        stop = (Button) view.findViewById(R.id.stop);
        pause = (Button) view.findViewById(R.id.pause);
//        back = (Button) view.findViewById(R.id.back);
//        forward = (Button) view.findViewById(R.id.forward);

        seekbar = (SeekBar)  view.findViewById(R.id.seekbar);
        seekbar.setClickable(false);

        player = new MediaPlayer();
        player.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build());

        try {
            player.setDataSource(this.getActivity().getApplicationContext(), file);
            player.prepare();
        }catch(Exception e)
        {
            e.printStackTrace();
            Log.e(TAG,"Error while trying to open URI:"+file);
        }

        // Set callback listener for events on the update button
        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                try {
                    if(player != null)
                        player.start();
                     else {
                        Toast.makeText(getActivity().getBaseContext(), "Select A File Again!", Toast.LENGTH_SHORT).show();
                    }

                } catch(Exception e)
                {
                    e.printStackTrace();
                    Log.e(TAG,"Error while trying to start audio player");
                }

                final_time = player.getDuration();
                start_time = player.getCurrentPosition();

                if(one_time_only == 0)
                {
                    seekbar.setMax((int) final_time);
                    one_time_only = 1;
                }

                seekbar.setProgress((int) start_time);
                handler.postDelayed(UpdateSongTime, 100);
                play.setEnabled(false);
//                stop.setEnabled(true);
                pause.setEnabled(true);
                Toast.makeText(getActivity().getBaseContext(), "Starting audio playback", Toast.LENGTH_SHORT).show();

            }

        });

//        stop.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v)
//            {
//
//                Toast.makeText(getActivity().getBaseContext(), "Stopping audio playback", Toast.LENGTH_SHORT).show();
//
//                play.setEnabled(false);
//                stop.setEnabled(false);
//                pause.setEnabled(false);
//                player.stop();
//            }
//
//        });

        pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity().getBaseContext(), "Pausing audio playback", Toast.LENGTH_SHORT).show();
                player.pause();
                play.setEnabled(true);
                pause.setEnabled(false);
//                stop.setEnabled(false);
            }

        });

//        back.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v)
//            {
//                //TODO: Implement backwards logic
//            }
//
//        });
//
//        forward.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v)
//            {
//                //TODO: Implement fast forward logic
//            }
//
//        });

        return view;
    }

    // Separate Thread for updating the seekbar object in parallel to the AudioPlayer execution logic.
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            start_time = player.getCurrentPosition();
            seekbar.setProgress((int)start_time);
            handler.postDelayed(this, 100);
        }
    };

    /**
     * Method for cleaning up the AudioPlayer fragment once it is popped from the process stack.
     */
    @Override
    public void onDestroy() {

        //TODO: Cleanup the audio player when removing this instance from fragment manager
        super.onDestroy();
        if (player != null) player.release();
    }

}
