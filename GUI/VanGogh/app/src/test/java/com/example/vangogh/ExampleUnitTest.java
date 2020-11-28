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
    public void getPreProcess() throws WavFileException, IOException, FileFormatNotSupportedException {
        float[] load = pp.loadmusic("C:\\Users\\johnm\\git\\VANGOGHS-EAR\\GUI\\VanGogh\\app\\src\\main\\java\\chords\\a.wav");
        float[][] gen = pp.genMelSpectrogram(load);
        Complex[][] stft = pp.generateSTFTFeatures(load);
//        for(int i = 0; i < load.length; i++) {
//            System.out.println(load[i]);
//        }
//        System.out.println("Now the gen: \n");
//        for (int i= 0; i < gen.length; i++) {
//                System.out.println(Arrays.deepToString(gen));
//        }
        System.out.println("Now the stft: \n");
        for (int i= 0; i < stft.length; i++) {
            for (int j = 0; j < stft[i].length; j++) {
                System.out.println(stft[i][j].getReal() + " " + stft[i][j].getImaginary() + "\n");
            }
        }
        assertEquals("" + -6.4E-4, "" + load[0]);
    }
}