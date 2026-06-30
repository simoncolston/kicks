package org.colston.kicks.document;

import jakarta.xml.bind.annotation.*;

import java.util.Objects;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(
        propOrder = {
                "title",
                "tuning",
                "tempo",
                "transcription"
        })
public class Song {
    @XmlAttribute
    private int index = 0;
    @XmlElement
    private String title;
    @XmlElement
    private Tuning tuning;
    @XmlElement
    private String tempo;
    @XmlElement
    private String transcription;

    @SuppressWarnings("unused")
    private Song() {
    }

    public Song(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Tuning getTuning() {
        return tuning;
    }

    public void setTuning(Tuning tuning) {
        this.tuning = tuning;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Song song = (Song) o;
        return index == song.index && Objects.equals(title, song.title) && tuning == song.tuning
                && Objects.equals(tempo, song.tempo) && Objects.equals(transcription, song.transcription);
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + Objects.hashCode(title);
        result = 31 * result + Objects.hashCode(tuning);
        result = 31 * result + Objects.hashCode(tempo);
        result = 31 * result + Objects.hashCode(transcription);
        return result;
    }
}
