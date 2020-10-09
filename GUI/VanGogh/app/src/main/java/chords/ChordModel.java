package chords;

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

        //Verify value of chord name is not null
        if(chord_class != null)
            this.setChordClass(chord_class);
        else
            this.setChordClass("");

    }

    public String getChordClass() {
        return this.chord_class;
    }

    public String getChordName(){
        return this.chord_name;
    }

    public void setChordName(String name){
        this.chord_name = name;
    }

    public void setChordClass(String clss){
        this.chord_class = clss;
    }

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
