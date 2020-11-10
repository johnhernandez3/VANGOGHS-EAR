package chords;

import com.dqt.libs.chorddroid.classes.Chord;

import java.util.ArrayList;

public class ChordFactory {
    private final String[] chord_names = {"A", "B","C","D", "E", "F", "G"};
    private final String[] chord_classes = {"Maj", "Min","#","7"};
    private ChordValidator validator;
    private ChordModel chord;

    public ChordFactory()
    {
        validator = new ChordValidator(chord_names,chord_classes);
    }

    //TODO: Filter out the non-existent chords for certain classes

    /**
     * Generates the valid chords our system accepts internally in the ChordFragment class
     * @return
     */
    public ArrayList<ChordModel> createChords()
    {
        ArrayList<ChordModel> chords = new ArrayList<>();
        for(String name : validator.validationArrayList(chord_names))
            for(String clss : validator.validationArrayList(chord_classes))
            {
                chord = new ChordModel(name, clss);
                if (validator.isValidChord(chord))
                    chords.add(chord);
            }

        return chords;
    }


}
