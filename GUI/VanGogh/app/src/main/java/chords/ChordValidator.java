package chords;


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

    public ArrayList<String> validationArrayList(String validators[])
    {
        ArrayList<String> res = new ArrayList<>();

        for(String  v  : validators)
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


}
