package com.example.vangogh;

public class Recognition
{

    private String id = "";
    private String chordname = "";
    private float confidence= 0.00f;

    public Recognition(String id, String chordname, float confidence) {
        this.id = id;
        this.chordname = chordname;
        this.confidence = confidence;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return chordname;
    }

    public float getConfidence() {
        return confidence;
    }
}
