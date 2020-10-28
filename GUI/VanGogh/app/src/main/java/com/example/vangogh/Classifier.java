//package com.example.vangogh;
//
//import android.app.Activity;
//import android.graphics.RectF;
//
//import java.io.IOException;
//import java.nio.MappedByteBuffer;
//import java.util.List;
//
////import org.tensorflow.lite.DataType;
////import org.tensorflow.lite.Interpreter;
//////import org.tensorflow.lite.examples.classification.env.Logger;
//////import org.tensorflow.lite.examples.classification.tflite.Classifier.Device;
////import org.tensorflow.lite.gpu.GpuDelegate;
////import org.tensorflow.lite.support.common.FileUtil;
////import org.tensorflow.lite.support.common.TensorOperator;
//
//public abstract class Classifier {
//
//    public Classifier() {
//
//    }
//
//    public enum Device{CPU,GPU};
//
////    protected Interpreter tflite;
//    private List<String> labels;
//
//    /** Number of results to show in the UI. */
//    private static final int MAX_RESULTS = 3;
//
//    /** The loaded TensorFlow Lite model. */
//    private MappedByteBuffer tfliteModel;
//
//    /** Options for configuring the Interpreter. */
////    private final Interpreter.Options tfliteOptions = new Interpreter.Options();
//
//    /** Output probability TensorBuffer. */
////    private final TensorBuffer outputProbabilityBuffer;
//
//    /** Optional GPU delegate for acceleration. */
//    // TODO: Declare a GPU delegate
//    private GpuDelegate gpuDelegate = null;
////
////    protected Classifier(Activity activity, Device device, int numThreads) throws IOException {
////
////        // TODO: Create a TFLite interpreter instance
////        tflite = new Interpreter(tfliteModel, tfliteOptions);
////    }
////
//    /** Closes the interpreter and model to release resources. */
//    public void close() {
//        if (tflite != null) {
//            // TODO: Close the interpreter
//            tflite.close();
//            tflite = null;
//        }
//        // TODO: Close the GPU delegate
//        if (gpuDelegate != null) {
//            gpuDelegate.close();
//            gpuDelegate = null;
//        }
//
//        tfliteModel = null;
//    }
//
//    /** An immutable result returned by a Classifier describing what was recognized. */
//    public static class Recognition
//    {
//        /**
//         * A unique identifier for what has been recognized. Specific to the class, not the instance of
//         * the object.
//         */
//        private final String id;
//
//        /** Display name for the recognition. */
//        private final String title;
//
//        /**
//         * A sortable score for how good the recognition is relative to others. Higher should be better.
//         */
//        private final Float confidence;
//
//        /** Optional location within the source image for the location of the recognized object. */
//        private RectF location;
//
//        public Recognition(
//                final String id, final String title, final Float confidence, final RectF location) {
//            this.id = id;
//            this.title = title;
//            this.confidence = confidence;
//            this.location = location;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public String getTitle() {
//            return title;
//        }
//
//        public Float getConfidence() {
//            return confidence;
//        }
//
//        public RectF getLocation() {
//            return new RectF(location);
//        }
//
//        public void setLocation(RectF location) {
//            this.location = location;
//        }
//
//        @Override
//        public String toString() {
//            String resultString = "";
//            if (id != null) {
//                resultString += "[" + id + "] ";
//            }
//
//            if (title != null) {
//                resultString += title + " ";
//            }
//
//            if (confidence != null) {
//                resultString += String.format("(%.1f%%) ", confidence * 100.0f);
//            }
//
//            if (location != null) {
//                resultString += location + " ";
//            }
//
//            return resultString.trim();
//        }
//    }
//
//    /**
//     * Creates a classifier with the provided configuration.
//     *
//     * @param activity The current Activity.
//     * @param device The device to use for classification.
//     * @param numThreads The number of threads to use for classification.
//     * @return A classifier with the desired configuration.
//     */
//    public static MusicalChordClassifierNet create(Activity activity, Device device, int numThreads)
//            throws IOException {
//
//        return new MusicalChordClassifierNet(activity, device, numThreads);
//    }
//
//    /** Initializes a {@code Classifier}. */
//    protected Classifier(Activity activity, Device device, int numThreads) throws IOException {
//        tfliteModel = FileUtil.loadMappedFile(activity, getModelPath());
//        switch (device) {
//            case GPU:
//                // TODO: Create a GPU delegate instance and add it to the interpreter options
////                gpuDelegate = new GpuDelegate();
////                tfliteOptions.addDelegate(gpuDelegate);
//                break;
//            case CPU:
//                break;
//        }
////        tfliteOptions.setNumThreads(numThreads);
////        // TODO: Create a TFLite interpreter instance
////        tflite = new Interpreter(tfliteModel, tfliteOptions);
//
//        // Loads labels out from the label file.
//        labels = FileUtil.loadLabels(activity, getLabelPath());
//
//        // Reads type and shape of input and output tensors, respectively.
//        int imageTensorIndex = 0;
////        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
////        imageSizeY = imageShape[1];
////        imageSizeX = imageShape[2];
//        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();
//        int probabilityTensorIndex = 0;
//        int[] probabilityShape =
//                tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
//        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();
//
//        // Creates the input tensor.
////        inputImageBuffer = new TensorImage(imageDataType);
//
//        // Creates the output tensor and its processor.
////        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
//
//        // Creates the post processor for the output probability.
////        probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();
//
////        LOGGER.d("Created a Tensorflow Lite Image Classifier.");
//    }
//
//    /** Gets the name of the model file stored in Assets. */
//    protected abstract String getModelPath();
//
//    /** Gets the name of the label file stored in Assets. */
//    protected abstract String getLabelPath();
//
//    /** Gets the TensorOperator to nomalize the input image in preprocessing. */
//    protected abstract TensorOperator getPreprocessNormalizeOp();
//
//    /**
//     * Gets the TensorOperator to dequantize the output probability in post processing.
//     *
//     * <p>For quantized model, we need de-quantize the prediction with NormalizeOp (as they are all
//     * essentially linear transformation). For float model, de-quantize is not required. But to
//     * uniform the API, de-quantize is added to float model too. Mean and std are set to 0.0f and
//     * 1.0f, respectively.
//     */
//    protected abstract TensorOperator getPostprocessNormalizeOp();
//}
