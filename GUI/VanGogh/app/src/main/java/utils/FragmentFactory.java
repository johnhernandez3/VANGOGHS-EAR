package utils;

import androidx.fragment.app.Fragment;

import com.example.vangogh.AudioRecorder;
import com.example.vangogh.ChordFragment;
import com.example.vangogh.TablatureFragment;

import java.util.List;
import java.util.Map;


public class FragmentFactory {

    public static Fragment createFragment(String type)
    {
        Fragment frag = null;
        switch(type)
        {
            case "TABLATURE":
                frag = new TablatureFragment();
                break;

            case "AUDIO RECORDER":
                frag = new AudioRecorder();
                break;

            case "CHORD":
                frag = new ChordFragment();
                break;
            default:
                frag = null;
                break;

        }

        return frag;
    }

}
