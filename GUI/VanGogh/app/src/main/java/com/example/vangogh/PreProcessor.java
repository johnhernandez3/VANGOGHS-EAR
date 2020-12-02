
package com.example.vangogh;

import android.util.Log;
import android.util.Pair;

import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.wavFile.WavFileException;

import org.apache.commons.math3.complex.Complex;
import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PreProcessor {

    public JLibrosa jl = new JLibrosa();
    public Interpreter interpret;

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

        return jl.generateMelSpectroGram(load, jl.getSampleRate(), jl.getN_fft(), jl.getN_mels(), jl.getHop_length());

    }

    public Complex[][] generateSTFTFeatures(float[] load) {

        //MFCC = Mel-frequency cepstral coefficients (MFCCs)
        return jl.generateSTFTFeatures(load, jl.getSampleRate(),40);

    }

    public float[] generatemeanMFCCval(float[][] melSpecVals) {

        //Mean of MFCC using Mel-Spectrogram Values
        return jl.generateMeanMFCCFeatures(melSpecVals, 40, jl.getN_fft());

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

    public ByteBuffer createTensors(Interpreter interpreter,float[][] melSpec)
    {
        
        int imgTensorIndex = 0;
        int probTensorIndex = 0;
    
        DataType  imgDataType = interpreter.getInputTensor(imgTensorIndex).dataType(); 

        int[] inArray = interpret.getInputTensor(imgTensorIndex).shape();
            int[] outArray = interpret.getOutputTensor(probTensorIndex).shape();
            DataType probDataType = interpret.getOutputTensor(probTensorIndex).dataType();
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 * melSpec.length * melSpec[0].length);

            for(int i = 0; i < melSpec.length; i++) {
                float[] arrVal = melSpec[i];
                int[] inShapeDim = {1, 1, melSpec[0].length, 1};
                TensorBuffer inTnsorBuffer = TensorBuffer.createDynamic(imgDataType);
                inTnsorBuffer.loadArray(arrVal, inShapeDim);
                ByteBuffer valInBuffer = inTnsorBuffer.getBuffer();
                byteBuffer.put(valInBuffer);
            }

            byteBuffer.rewind();
            TensorBuffer outTensorBuffer = TensorBuffer.createFixedSize(outArray, probDataType);

            return byteBuffer;
    }


    public String InterpreterBuilder(File tensorFile, float[][] melSpec) {
        String output = new String();
        int imgTensorIndex = 0, probTensorIndex = 0;
        try {
            interpret = new Interpreter(tensorFile);
            DataType imgDataType = interpret.getInputTensor(imgTensorIndex).dataType();
            int[] inArray = interpret.getInputTensor(imgTensorIndex).shape();
            int[] outArray = interpret.getOutputTensor(probTensorIndex).shape();
            DataType probDataType = interpret.getOutputTensor(probTensorIndex).dataType();
            ByteBuffer byteBuffer = this.createTensors(interpret, melSpec);
            // ByteBuffer byteBuffer = ByteBuffer.allocate(4 * melSpec.length * melSpec[0].length);

            // for(int i = 0; i < melSpec.length; i++) {
            //     float[] arrVal = melSpec[i];
            //     int[] inShapeDim = {1, 1, melSpec[0].length, 1};
            //     TensorBuffer inTnsorBuffer = TensorBuffer.createDynamic(imgDataType);
            //     inTnsorBuffer.loadArray(arrVal, inShapeDim);
            //     ByteBuffer valInBuffer = inTnsorBuffer.getBuffer();
            //     byteBuffer.put(valInBuffer);
            // }

            // byteBuffer.rewind();
            TensorBuffer outTensorBuffer = TensorBuffer.createFixedSize(outArray, probDataType);

            interpret.run(byteBuffer, outTensorBuffer.getBuffer());
            interpret.close();
        } catch(Exception e) {
            Log.e("TAG", "Error preparing Interpreter and Tensors:" + e);
            e.printStackTrace();
        }
        return output;
    }

}
