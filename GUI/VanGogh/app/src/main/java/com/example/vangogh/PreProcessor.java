
package com.example.vangogh;

import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.wavFile.WavFileException;

import org.apache.commons.math3.complex.Complex;

import java.io.IOException;

public class PreProcessor {

    public PreProcessor() {}

    JLibrosa jl = new JLibrosa();


    public float[] loadmusic(String filename) throws FileFormatNotSupportedException, IOException, WavFileException {

        float[] load = jl.loadAndRead(filename, jl.getSampleRate(),jl.getNoOfFrames()/jl.getSampleRate());
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

}