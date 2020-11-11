package com.example.vangogh;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AudioRecorderTest {
    AudioRecorder audio_rec;


    @Test
    public void empty_filename_file_extension_isCorrect()
    {
        audio_rec = new AudioRecorder();
        String expected_file_output = "sample.3gp";
        String test = audio_rec.OutputFilePath("");
        assertEquals(expected_file_output, test);
        audio_rec = null;
    }

    @Test
    public void non_empty_filename_file_extension_isCorrect()
    {
        audio_rec = new AudioRecorder();
        String expected_file_output = "a.3gp";
        String test = audio_rec.OutputFilePath("a");
        assertEquals(expected_file_output, test);
        audio_rec = null;
    }

    @Test
    public void non_empty_string_isCorrect()
    {
        audio_rec = new AudioRecorder();

        assertTrue(audio_rec.nonEmptyString("a"));
    }

    @Test
    public void empty_whitespace_string_isCorrect()
    {
        audio_rec = new AudioRecorder();

        assertFalse(audio_rec.nonEmptyString(" "));
    }

    @Test
    public void empty_string_isCorrect()
    {
        audio_rec = new AudioRecorder();

        assertFalse(audio_rec.nonEmptyString(""));
    }


}