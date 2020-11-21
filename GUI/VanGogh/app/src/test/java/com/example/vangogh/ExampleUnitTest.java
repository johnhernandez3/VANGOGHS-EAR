package com.example.vangogh;


import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    ChordFragment cf = new ChordFragment();

    @Test
    public void getChordFileisCorrect() throws Exception {
        System.out.println(cf.getChordFile("g.wav").getAbsolutePath());
        assertEquals("\\src\\main\\java\\chords\\a.wav", cf.getChordFile("g.wav").getAbsolutePath());
    }
}