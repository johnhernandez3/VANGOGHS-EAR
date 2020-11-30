package com.example.vangogh;


import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.wavFile.WavFileException;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    PreProcessor pp = new PreProcessor();

    @Test
    public void getChordFileisCorrect() throws Exception {
//        System.out.println(cf.getChordFile("g.wav").getAbsolutePath());
//        assertEquals("\\src\\main\\java\\chords\\a.wav", cf.getChordFile("g.wav").getAbsolutePath());
    }
}