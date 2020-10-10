package chords;

import java.util.regex.*;
import java.util.ArrayList;

/**
 * Class designated to enforcing valid ChordModel policies based on internal RegEx filters.
 */
public class ChordValidator
{

    //Internal immutable validators for names and classes
    private final String[] chord_names ;
    private final String[] chord_classes ;

    /**
     * Constructor for building ChordValidator Object
     * @param names String[] for validating correct Chord Names
     * @param clss String[] for validating correct Chord Classes
     */
    public ChordValidator(String[] names, String[] clss)
    {
        this.chord_names =names;
        this.chord_classes=clss;
    }

    /**
     * Constructor for building ChordValidator Object
     * @param valid_chords ArrayList<ChordModel> for validating ChordNames and Classes
     */
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

    /**
     *  Generates internal validators for filtering out ChordModels.
     * @param validators String[] representing the basis for filtering invalid ChordModels
     * @return ArrayList<String> representing the valid ChordModel features.
     */
    public ArrayList<String> validationArrayList(String validators[])
    {
        ArrayList<String> res = new ArrayList<>();

        for(String  v  : validators)
            res.add(v);

        return res;
    }

    /**
     *  Generates internal validators for filtering out ChordModels.
     * @param validators ArrayList<ChordModel>  representing the basis for filtering invalid ChordModels
     * @return ArrayList<ChordModel> representing the valid ChordModel features.
     */
    public ArrayList<ChordModel> validationArrayList(ArrayList<ChordModel> validators)
    {
        ArrayList<ChordModel> res = new ArrayList<>();

        for(ChordModel  v  : validators)
        {
            res.add(v);
        }

        return res;
    }

    /**
     *  Internal method for Valid Chord Names ArrayList based filtering.
     * @return ArrayList<String> Representing the internal Valid Chord Names
     */
    private ArrayList<String> validChordNames()
    {
        return validationArrayList(chord_names);
    }

    /**
     *  Internal method for Valid Chord Class ArrayList based filtering.
     * @return ArrayList<String> Representing the internal Valid Chord Classes.
     */
    private ArrayList<String> validChordClass()
    {
        return validationArrayList(chord_classes);
    }

    /**
     * Verifies if the @param name provided is a valid Chord Name
     * @param name String representing the Chord Name to be evaluated
     * @return boolean representing the validity
     */
    public boolean isValidChordName(String name)
    {
        for(String chord: this.validChordNames())
        {
            if(chord.equalsIgnoreCase(name))
                return true;
        }

        return false;
    }

    /**
     * Verifies if the @param clss provided is a valid Chord Class
     * @param clss String representing the Chord Class to be evaluated
     * @return boolean representing the validity
     */
    public boolean isValidChordClass(String clss)
    {
        for(String cl: this.validChordClass())
        {
            if(cl.equalsIgnoreCase(clss))
                return true;
        }

        return false;
    }

    /**
     * Verifies if the @param concatenated_string provided is a valid ChordModel.
     * @param concatenated_string String representing the ChordModel to be evaluated
     * @return boolean representing the validity
     */
    public boolean isValidChord(String concatenated_string)
    {
        //TODO: implement Regex here to detect correct chords
        Pattern pattern = this.chordPattern();
        Matcher target = pattern.matcher(concatenated_string);

        //This implementation causes a bug where even just having one character will short circuit
//        if(target.find())
//        {
//            return true;
//        }
        if(target.matches())
            return true;
        return false;
    }

    /**
     * Verifies if the @param chord provided is a valid ChordModel.
     * @param chord ChordModel representing the Chord to be evaluated.
     * @return boolean representing the validity.
     */
    public boolean isValidChord(ChordModel chord)
    {
        if(this.isValidChordClass(chord.getChordClass())
                && this.isValidChordName(chord.getChordName()))
            return true;
        else{
            return false;
        }
    }

    //Regex Logic for matching patterns in user input

    /**
     *  Extracts a valid ChordModel if @param concatenated_string is found to match the internal ChordModel pattern.
     *  Otherwise, the method returns an invalid ChordModel with Name="Z" and Class="Z".
     * @param concatenated_string String generated by user input that may contain a ChordModel compliant pattern.
     * @return ChordModel representation of @param concatenated_string if a valid pattern is found.
     */
    public ChordModel extractChord(String concatenated_string)
    {
        ChordModel res;
        Pattern pattern = this.chordPattern();
        Matcher target = pattern.matcher(concatenated_string);
        if(target.find())
        {
            int start = target.start();
            int end = target.end();
            String intermediate = concatenated_string.substring(start,end);
            System.out.println(intermediate);
            res = new ChordModel(extractChordName(intermediate),
                    extractChordClass(intermediate));
        }
        else{
            //invalid chord
            res = new ChordModel("Z", "Z");
        }
        return res;
    }

    /**
     *  Returns an internal RegEx Pattern used for detecting valid Chords to enforce ChordModel validity .
     *
     * @return Pattern representation of  a valid ChordModel pattern.
     */
    private Pattern chordPattern()
    {

        return Pattern.compile(stringPattern());
    }

    /**
     *  Extracts a valid ChordModel name if @param whole_chord is found to match the internal ChordModel Name pattern.
     *  Otherwise, the method returns an invalid ChordModel with Name="Z".
     * @param whole_chord String generated by user input that may contain a ChordModel compliant pattern.
     * @return String representation of matched substring found in @param whole_chord if a valid pattern is found.
     *          Otherwise, the resulting string is "Z".
     */
    private String extractChordName(String whole_chord)
    {
        Pattern pattern = Pattern.compile("([abcdefg]|[ABCDEFG])");
        Matcher target = pattern.matcher(whole_chord);

        if(target.find())
            return target.group();

        else
            return "Z";//No Match Found
    }

    /**
     *  Extracts a valid ChordModel Class if @param whole_chord is found to match the internal ChordModel Class pattern.
     *  Otherwise, the method returns an invalid ChordModel with Class="Z".
     * @param whole_chord String generated by user input that may contain a ChordModel compliant pattern.
     * @return String representation of matched substring found in @param whole_chord if a valid pattern is found.
     *          Otherwise, the resulting string is "Z".
     */
    private String extractChordClass(String whole_chord)
    {
        Pattern pattern = Pattern.compile("[7]?[#]?(([M][a][j])|([M][i][n]))?");
        Matcher target = pattern.matcher(whole_chord);

        if(target.find())
            return target.group();

        else
            return "";//No Match Found
    }


    /**
     *  Returns an internal RegEx Pattern used for detecting valid Chords to enforce ChordModel validity .
     *
     * @return String representation of  a valid ChordModel pattern.
     */
    private String stringPattern()
    {
        String str_pattern = "([abcdefg]|[ABCDEFG])[7]?[#]?(([M][a][j])|([M][i][n]))?";



        return str_pattern;
    }





}
