package chords;

import java.util.regex.*;
import java.util.ArrayList;

public class ChordValidator
{

    private final String[] chord_names ;
    private final String[] chord_classes ;

    public ChordValidator(String[] names, String[] clss)
    {
        this.chord_names =names;
        this.chord_classes=clss;
    }

    public ChordValidator(ArrayList<ChordModel> valid_chords)
    {
        //Used as intermediate since we cant re-assign to final variables
        ArrayList<String> partial_names = new ArrayList<>();
        ArrayList<String> partial_classes = new ArrayList<>();

        for(ChordModel chord: valid_chords)
        {
            //if current chord class not already found in partial_classes,
            // add it to the array list
            if(!partial_classes.contains(chord.getChordClass()))
                partial_classes.add(chord.getChordClass());

            //if current chord name not already found in partial_names,
            // add it to the array list
            if(!partial_names.contains(chord.getChordName()))
                partial_names.add(chord.getChordName());
        }

        // Cast the ArrayLists to String Arrays and assign those to final vars in this class.
        chord_names = (String[]) partial_names.toArray(new String[partial_names.size()]);
        chord_classes = (String[]) partial_classes.toArray(new String[partial_classes.size()]);
    }


    public ArrayList<String> validationArrayList(String validators[])
{
    ArrayList<String> res = new ArrayList<>();

    for(String  v  : validators)
    {
        res.add(v);
    }

    return res;
}

    public ArrayList<ChordModel> validationArrayList(ArrayList<ChordModel> validators)
    {
        ArrayList<ChordModel> res = new ArrayList<>();

        for(ChordModel  v  : validators)
        {
            res.add(v);
        }

        return res;
    }




    private ArrayList<String> validChordNames()
    {
        return validationArrayList(chord_names);
    }

    private ArrayList<String> validChordClass()
    {
        return validationArrayList(chord_classes);
    }

    public boolean isValidChordName(String name)
    {
        for(String chord: this.validChordNames())
        {
            if(chord.equalsIgnoreCase(name))
                return true;
        }

        return false;
    }

    public boolean isValidChordClass(String clss)
    {
        for(String cl: this.validChordClass())
        {
            if(cl.equalsIgnoreCase(clss))
                return true;
        }

        return false;
    }


    public boolean isValidChord(ChordModel chord)
    {
        if(this.isValidChordClass(chord.getChordClass())
                && this.isValidChordName(chord.getChordName()))
            return true;
        else{
            return false;
        }
    }

    private String stringPattern()
    {
        String str_pattern = "([abcdefg]|[ABCDEFG])[7]?[#]?(([M][a][j])|([M][i][n]))?";



        return str_pattern;
    }

    private Pattern chordPattern()
    {

        return Pattern.compile(stringPattern());
    }

    private String extractChordName(String whole_chord)
    {
        Pattern pattern = Pattern.compile("([abcdefg]|[ABCDEFG])");
        Matcher target = pattern.matcher(whole_chord);

        if(target.find())
            return target.group();

        else
            return "";//No Match Found
    }
    private String extractChordClass(String whole_chord)
    {
        Pattern pattern = Pattern.compile("[7]?[#]?(([M][a][j])|([M][i][n]))?");
        Matcher target = pattern.matcher(whole_chord);

        if(target.find())
            return target.group();

        else
            return "";//No Match Found
    }



    public ChordModel extractChord(String concatenated_string)
    {
        ChordModel res;
        Pattern pattern = this.chordPattern();
        Matcher target = pattern.matcher(concatenated_string);
        if(target.find())
        {
            res = new ChordModel(extractChordName(target.group()),
                                 extractChordClass(target.group()));
        }
        else{
            //invalid chord
            res = new ChordModel("Z", "Z");
        }
        return res;
    }

    public boolean isValidChord(String concatenated_string)
    {
        //TODO: implement Regex here to detect correct chords
        Pattern pattern = this.chordPattern();
        Matcher target = pattern.matcher(concatenated_string);

        if(target.find())
        {
            return true;
        }
        return false;
    }



}
