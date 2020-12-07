package chords;

import java.util.ArrayList;

/**
 * Class used to represent internally an ASCII Tablature for use in the TablatureFragment class
 */
public class Tablature
{
    private final String[] GUITAR_STRINGS =  {"E", "A", "D", "G", "B","E"};
    private ArrayList<ChordModel> chords;
    private ArrayList<Integer> frets;

    public Tablature(ArrayList<ChordModel> chords)
    {
        super();
        if(chords.size() > 0)
            this.chords = chords;
        else{
            this.chords = new ArrayList<>();
        }
    }

    public Tablature()
    {
        prepareFrets();
    }

    
    /**
     * Convert the internally stored ChordModels into Tablature Notation String representations.
     * @return String chords in Tablature Notation format.
     */
    private String convertChords()
    {
        return ChordToTab.convertChords(this.chords);
    }
    /**
     * String representation of a Tablature
     * @return String representation of a Tablature
     */
    @Override
    public String toString()
    {
        //TODO: String representation of a Tablature
//        String repr = "";
        // StringBuilder str_builder = new StringBuilder();

        // for(String g_string: GUITAR_STRINGS)
        // {
        //     str_builder.append(g_string);
        //     for(int i : frets)
        //     {
        //         str_builder.append("-");
        //         str_builder.append("|");
        //     }
        //     str_builder.append("-");
        //     str_builder.append("\n");
        // }

        // return str_builder.toString();
        
        return ChordToTab.convertChords(chords);
    }

    /**
     * Generates the internal list of frets to be used when creating a Tablature instance.
     */
    private void prepareFrets()
    {
        frets = new ArrayList<>();
        //TODO: May be wrong here on the frets existing on a guitar...
        for(int i=1; i < 13; i++)
        {
            frets.add(i);
        }
    }


}
