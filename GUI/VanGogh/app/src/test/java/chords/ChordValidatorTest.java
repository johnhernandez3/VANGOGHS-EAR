package chords;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ChordValidatorTest {

    private  ChordValidator validator;
    private ChordFactory chord_factory;

    public List<String> generateCorrectChords()
    {
        List<String> chords = new ArrayList<>();

        chords.add("A");
        chords.add("Amin");
        chords.add("B");
        chords.add("Bmin");
        chords.add("C");
        chords.add("Dmin");
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
    public void validateCorrectChords()
    {
        chord_factory = new ChordFactory();
        validator = new ChordValidator(chord_factory.createChords());

        List<String> expected_correct_chords = generateCorrectChords();

        for (String chord : expected_correct_chords)
        {
            assertTrue(validator.isValidChord(chord));
        }

    }

    @Test
    public void validateIncorrectChords()
    {
        chord_factory = new ChordFactory();
        validator = new ChordValidator(chord_factory.createChords());

        List<String> expected_incorrect_chords = generateIncorrectChords();

        for (String chord : expected_incorrect_chords)
        {
            assertFalse(validator.isValidChord(chord));
        }
    }

}
