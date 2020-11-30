
package com.example.vangogh;

import android.util.Pair;

import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.wavFile.WavFileException;

import org.apache.commons.math3.complex.Complex;
import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PreProcessor {

    public JLibrosa jl = new JLibrosa();

    public PreProcessor() {}



    public float[] loadmusic(String filename) throws FileFormatNotSupportedException, IOException, WavFileException {
        int durationInSeconds = -1;
        File ff = new File(filename);
        float[] load = new float[1];
        if(ff.exists()) {
            load = jl.loadAndRead(filename, jl.getSampleRate(),durationInSeconds);
        }
        return load;
    }

    public float[][] genMelSpectrogram(float[] load) {

        float[][] plot = jl.generateMelSpectroGram(load, jl.getSampleRate(), jl.getN_fft(), jl.getN_mels(), jl.getHop_length());
        return plot;

    }

    public Complex[][] generateSTFTFeatures(float[] load) {

        //MFCC = Mel-frequency cepstral coefficients (MFCCs)
        Complex[][] stft = jl.generateSTFTFeatures(load, jl.getSampleRate(),40);
        return stft;

    }

    public Pair[] convertPolar(Complex[][] comp){
        int counter = 0;
        for(int n = 0; n < comp.length; n++) {
            counter += comp[n].length;
        }
        double[] r = new double[counter];
        double[] theta = new double[counter];
        int k = 0;
        ArrayList<Pair> p = new ArrayList<Pair>();
        for(int i = 0; i < comp.length; i++) {
            for(int j = 0; j < comp[i].length; j++) {
                if(k < counter) {
                    r[k] = Math.sqrt(comp[i][j].getReal() * comp[i][j].getReal()) + (comp[i][j].getImaginary() * comp[i][j].getImaginary());
                    theta[k] = Math.atan(comp[i][j].getImaginary() / comp[i][j].getReal());
                    p.add(new Pair(r[k], theta[k]));
                    k++;
                }
            }
        }
        return (Pair[]) p.toArray();
    }

    public Object InterpreterBuilder(File tensorFile, Object input) {
        Object output = new Object();
        try(Interpreter interpret = new Interpreter(tensorFile)) {
            interpret.run(input, output);
            interpret.close();
        }
        return output;
    }

}
