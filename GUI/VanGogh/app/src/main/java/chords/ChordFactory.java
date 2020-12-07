package chords;
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

    public ArrayList<ChordModel> createValidInternalChords()
    {
        ArrayList<ChordModel> valid_chords = new ArrayList<>();

//        for(String chord: chord_names)
//        {
//            valid_chords.add(new ChordModel(chord, ""));
//            if(chord == "A" || chord == "B" || chord == "D" || chord == "E" )
//                valid_chords.add(new ChordModel(chord, "m"));
//        }
        valid_chords.add(new ChordModel("A",""));
        valid_chords.add(new ChordModel("A","m"));
        valid_chords.add(new ChordModel("B","m"));
        valid_chords.add(new ChordModel("C",""));
        valid_chords.add(new ChordModel("D",""));
        valid_chords.add(new ChordModel("D","m"));
        valid_chords.add(new ChordModel("E",""));
        valid_chords.add(new ChordModel("E","m"));
        valid_chords.add(new ChordModel("F",""));
        valid_chords.add(new ChordModel("G",""));
        return valid_chords;
    }



}
