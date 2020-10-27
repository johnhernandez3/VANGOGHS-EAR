package chords;

import java.util.ArrayList;

public class Tablature
{
    private final String[] GUITAR_STRINGS =  {"E", "A", "D", "G", "B","E"};
    private ArrayList<String> chords;
    private ArrayList<Integer> frets;

    public Tablature()
    {
        prepareFrets();
    }

    @Override
    public String toString()
    {
        //TODO: String representation of a Tablature
//        String repr = "";
        StringBuilder str_builder = new StringBuilder();

        for(String g_string: GUITAR_STRINGS)
        {
            str_builder.append(g_string);
            for(int i : frets)
            {
                str_builder.append("-");
                str_builder.append("|");
            }
            str_builder.append("-");
            str_builder.append("\n");
        }

        return str_builder.toString();
    }

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
