package chords;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ChordModelTest {


    @Test
    public void createModel_ValidParams_isCorrect()
    {
        String name = "A";
        String clss = "Maj";

        ChordModel chord = new ChordModel(name,clss);
        assertEquals(chord.getChordClass(),clss);
        assertEquals(chord.getChordName(), name);
    }

    @Test
    public void createModel_InvalidParams_isCorrect()
    {
        String name = null;
        String clss = null;

        ChordModel chord = new ChordModel(name,clss);
        assertEquals(chord.getChordClass(),clss);
        assertEquals(chord.getChordName(), name);
    }

    @Test
    public void setParams_ValidParams_isCorrect()
    {
        String name = "A";
        String clss = "Maj";

        ChordModel chord = new ChordModel(name,clss);
        assertEquals(chord.getChordClass(),clss);
        assertEquals(chord.getChordName(), name);

        String change_name = "B";
        String change_class = "Min";

        chord.setChordName(change_name);
        chord.setChordClass(change_class);

        assertNotEquals(chord.getChordClass(),clss);
        assertNotEquals(chord.getChordName(), name);
        assertEquals(chord.getChordClass(),change_class);
        assertEquals(chord.getChordName(), change_name);
    }

    @Test
    public void setParams_InvalidParams_isCorrect()
    {
        String name = "A";
        String clss = "Maj";

        ChordModel chord = new ChordModel(name,clss);
        assertEquals(chord.getChordClass(),clss);
        assertEquals(chord.getChordName(), name);

        String change_name = null;
        String change_class = null;

        chord.setChordName(change_name);
        chord.setChordClass(change_class);

        assertNotEquals(chord.getChordClass(),clss);
        assertNotEquals(chord.getChordName(), name);
        assertEquals(chord.getChordClass(),change_class);
        assertEquals(chord.getChordName(), change_name);
    }

}