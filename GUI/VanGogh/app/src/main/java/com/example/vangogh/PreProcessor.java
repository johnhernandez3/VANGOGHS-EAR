//
//package com.example.vangogh;
//
//import com.jlibrosa.audio.JLibrosa;
//import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
//import com.jlibrosa.audio.wavFile.WavFileException;
//
//import org.apache.commons.math3.complex.Complex;
//
//import java.io.IOException;
//
//public class PreProcessor {
//
//   public PreProcessor() {}
//
//   JLibrosa jl = new JLibrosa();
//
//
//   public Complex[][] loadmusic(String filename) throws FileFormatNotSupportedException, IOException, WavFileException {
//
//      float[] load = jl.loadAndRead(filename, jl.getSampleRate(),jl.getNoOfFrames()/jl.getSampleRate());
//      float[][] plot = jl.generateMelSpectroGram(load, jl.getSampleRate(), jl.getN_fft(), jl.getN_mels(), jl.getHop_length());
//      //MFCC = Mel-frequency cepstral coefficients (MFCCs)
//      Complex[][] stft = jl.generateSTFTFeatures(load, jl.getSampleRate(),40);
//      return stft;
//   }
//
//}