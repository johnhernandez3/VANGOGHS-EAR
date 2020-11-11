package chords;

/**
 * Used for internal representation of Chord for processing incoming input from the user and Classifier requests.
 */
public class ChordModel
{
    //TODO: Implement parameter checks to avoid parsing Null values
    private String chord_name;
    private String chord_class;

    public ChordModel(String chord_name, String chord_class)
    {
        //Verify value of chord name is not null
        if(chord_name != null)
            this.setChordName(chord_name);
        else
            this.setChordName("");

        //Verify value of chord class is not null
        if(chord_class != null)
            this.setChordClass(chord_class);
        else
            this.setChordClass("");
    }

    /**
     *  Returns the internally stored Chord Class
     * @return String representation of the class for this chord.
     */
    public String getChordClass() {
        return this.chord_class;
    }

    /**
     *  Returns the internally stored Chord Name
     * @return String representation of the name for this chord.
     */
    public String getChordName(){
        return this.chord_name;
    }

    /**
     * Assigns the @param name for this instance of ChordModel
     * @param name String representation  of the chord's name.
     */
    public void setChordName(String name){
        this.chord_name = name;
    }


    /**
     * Assigns the @param clss for this instance of ChordModel
     * @param clss String representation  of the chord's class.
     */
    public void setChordClass(String clss){
        this.chord_class = clss;
    }

    /**
     * String representation of the ChordModel in the format of "Chord Name Chord Class"
     * @return String representation of the ChordModel
     */
    @Override
    public String toString()
    {
        // Uses string builder due to the performance penalty incurred when concatenating strings
        StringBuilder str_build = new StringBuilder();
        str_build.append(this.chord_name);
        str_build.append(this.chord_class);

        return str_build.toString();
    }





}
