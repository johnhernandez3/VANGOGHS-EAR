package chords;

import androidx.core.util.Pair;


import java.util.ArrayList;

/**
 * Class for Converting Chords to Tablature Notation
 */
public class ChordToTab {

    //These are the chords we internally recognize with our model
    private String[] internal_valid_chords = {"a", "am", "bm","c","d","dm","e","em","f","g"};
    private ChordFactory chord_factory;

    public ChordToTab()
    {
        this.chord_factory = new ChordFactory();
    }


    public ArrayList<Pair<Integer, Integer>> constructFretFingerPair(String chord)
    {
        ArrayList<Pair<Integer, Integer>> pairs = new ArrayList<>();
        Position[] positions = ChordLibrary.baseChords.get(chord);
        if(positions.length > 0)
        {
            //If the chord exists in our database then,
            for(int i=0 ; i < positions[0].frets.length ; i++)
            {
                pairs.add(new Pair<>(positions[0].frets[i],positions[0].fingers[i] ));
            }
        }
        //Else return an empty List

        return pairs;
    }

    public static String[]  addGuitarStrings(String[] tab_columns)
    {
        String[] with_g_chords = new String[tab_columns.length + 1];
        with_g_chords[0] = "EADGBe";
        for(int i=0 ; i < tab_columns.length ; i++)
        {
            with_g_chords[i+1]=tab_columns[i];
        }

        return with_g_chords;
    }

    /**
     *  Converts @param chords to their respective Tablature Notation in the FIFO order they appear in.
     * @param chords The ChordModel representation of chords we wish to convert to Tablature Notation.
     * @return String Representation of the Tablature Notation chords.
     */
    public static String convertChords(ArrayList<ChordModel> chords)
    {
        if(chords == null)
            return "";

        if(chords.size() <1 )
            return "";
        ArrayList<String> str_chords = new ArrayList<>();

        for(ChordModel chord : chords)
        {
            str_chords.add(chord.toString());
        }

        if(str_chords.size() < 0)
            return "";

        String[] columns = ChordToTab.constructTab(chords.toArray(new String[chords.size()]));

        String total_tablature = ChordToTab.totalTablature(ChordToTab.addGuitarStrings(columns));

        return total_tablature;
    }

     public static String convertStringChords(ArrayList<String> chords)
     {
         if(chords.size() <= 0)
             return "";

         String[] columns = ChordToTab.constructTab(chords.toArray(new String[chords.size()]));

         String total_tablature = ChordToTab.totalTablature(ChordToTab.addGuitarStrings(columns));
         // System.out.println("Total Chord Tab Generated:\n"+ total_tablature + "\n");

         return total_tablature;
     }

    // -1 => X therefore not strung
    //Positions are created by constructor as: position:int, frets:int[], fingers:int[]
    // the position int is the fret on the guitar itself,
    // the frets[] is the guitar strings in that fret held
    // the fingers[] is the fingers which will hold said fret

    /**
     *  Transform received @param chord to a Tablature Notation representation of the Chord associated with it in ChordLibrary Class.
     * @param chord String representation of the chord whose Tablature Notation we desire.
     * @return String representing the Tablature Notion of @param chord in a column format.
     */
    public static String toTablatureNotation(String chord)
    {
        StringBuilder str_builder = new StringBuilder();
        ChordValidator c_validator = new ChordValidator(new ChordFactory().createValidInternalChords());
        ChordModel cm_chord = c_validator.extractChord(chord);

        if(cm_chord.toString()== "ZZ")
            return "";

        Position[] positions = ChordLibrary.baseChords.get(cm_chord.toString());
        //we'll just use the first one and that's it
        if(positions.length > 0)
        {
            //Each fret is a vertical line followed by a newline

                for(int fret: positions[0].frets)
                {
                    if(fret != -1)
                    {
                        str_builder.append(fret);
                    }
                    else{
                        str_builder.append("X");
                    }

                }
        }

        return str_builder.toString();
    }

    /**
     * Construct the Tablature Notation of the @param chords provided.
     * @param chords String representation of chords to be transformed into Tablature Notation
     * @return String[] representing columns of Tablature Notation chords to be used later in building the complete Tablature.
     */
    public static String[] constructTab(String[] chords)
    {
        String[] result = new String[chords.length];

        for(int i= 0; i < chords.length; i++)
        {
            result[i] = toTablatureNotation(chords[i]);
        }

        return result;
    }

    /**
     * Calculates the transpose matrix of @param tab_columns that is used internally to generate a String representation of a Tablature.
     * @param tab_columns String array representing the Tablature Notation of chords.
     * @return the transpose matrix of @param tab_columns.
     */
    public static String[][] stringTransponse(String[] tab_columns)
    {
        String[][] intermediate = new String[tab_columns[0].length()][tab_columns.length];

        for(int i = 0; i < tab_columns[0].length(); i++) {
            for(int j = 0; j < tab_columns.length; j++) {
                char temp = tab_columns[j].charAt(i);
                intermediate[i][j] = Character.toString(temp);

            }
        }

        return intermediate;
    }

    /**
     * Generates the complete tablature of the received @param tab_columns arrays.
     * @param tab_columns String array representing the Tablature Notation of chords.
     * @return String representation of the complete ASCII Tablature with correct formatting.
     */
    public static String totalTablature(String[] tab_columns)
    {
        StringBuilder str_builder = new StringBuilder();

        String[][] intermediate = stringTransponse(tab_columns);

        for( int i = 0 ; i < intermediate.length ; i++)
        {
            for(int j = 0; j < intermediate[0].length; j++)
            {
                str_builder.append(intermediate[i][j]);
                str_builder.append("---");
            }

            str_builder.append("\n");

        }

        return str_builder.toString();
    }



}
