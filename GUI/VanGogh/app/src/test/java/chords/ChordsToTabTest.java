package chords;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ChordsToTabTest{

    private  ChordValidator validator;
    private ChordFactory chord_factory;

    public List<String> generateCorrectChords()
    {
        List<String> chords = new ArrayList<>();

        chords.add("A");
        chords.add("Am");
        chords.add("B");
        chords.add("Bm");
        chords.add("C");
        chords.add("Dm");
        chords.add("E");
        chords.add("F");
        chords.add("G");

        return chords;
    }

    public List<String> generateIncorrectChords()
    {
        List<String> chords = new ArrayList<>();

        String [] chord_names = {"H", "I","J","K","L","M","N","O",
                                "P","Q","R","S","T","U","V","W",
                                "X","Y","Z","0","1","2","3","4",
                                "5","6","7","8","9","!","@","#",
                                "$","%","^","&","*","(",")","{","}",
                                "[","]","<",">",",",".","/","\\","?","`","~"};
        String[] chord_classes = {"H", "I","J","K","L","M","N","O",
                                "P","Q","R","S","T","U","V","W",
                                "X","Y","Z","!","@",
                                "$","%","^","&","*","(",")","{","}",
                                "[","]","<",">",",",".","/","\\","?","`","~",
                                "Mij","Muj","Moj","Myj","Mej",
                                "Man","Mon","Mun","Myn","Men"};

        for(String names: chord_names)
        {
            for(String clss : chord_classes)
            {
                chords.add(names+clss);
            }
        }

        return chords;
    }

    @Test
    public void testTranspose4x4()
    {
        String matrix[] = {"1234",
                "1234",
                "1234",
        "1234"};
        String expected[][] = {{"1","1","1","1"},
                {"2","2","2","2"},
                {"3","3","3","3"},
                {"4","4","4","4"}};

        String[][] transpose = ChordToTab.stringTransponse(matrix);

        assertArrayEquals(expected,transpose);
    }

    @Test
    public void testTranspose3x3()
    {
        String matrix[] = {"123",
                            "123",
                            "123"};
        String expected[][] = {{"1","1","1"},
                            {"2","2","2"},
                            {"3","3","3"}};        

        String[][] transpose = ChordToTab.stringTransponse(matrix);

        assertArrayEquals(expected,transpose);
    }

    @Test
    public void testTranspose2x2()
    {
        String matrix[] = {"12",
                "12"};
        String expected[][] = {{"1","1"},
                {"2","2"}};
        String[][] transpose = ChordToTab.stringTransponse(matrix);
        assertArrayEquals(expected,transpose);
    }

    @Test
    public void testTranspose1x1()
    {
        String matrix[] = {"1"};
        String expected[][] = {{"1"}};
        String[][] transpose = ChordToTab.stringTransponse(matrix);
        assertArrayEquals(expected,transpose);
    }

    @Test
    public void validateChordsToTabs()
    {
        chord_factory = new ChordFactory();
        validator = new ChordValidator(chord_factory.createChords());

        List<String> expected_correct_chords = generateCorrectChords();

        List<String[]> tab_columns = new ArrayList<>();

        List<String> tabs = new ArrayList<>();



        String[] columns = ChordToTab.constructTab(expected_correct_chords.toArray(new String[expected_correct_chords.size()]));

        String total_tablature = ChordToTab.totalTablature(ChordToTab.addGuitarStrings(columns));
        System.out.println("Total Chord Tab Generated:\n"+ total_tablature + "\n");
    }
}
