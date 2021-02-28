package org.colston.kicks.document;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class Song {
    @XmlAttribute
    private int index = 0;
    @XmlElement
    private String title;
    @XmlElement
    private Tuning tuning;
    @XmlElement
    private String tempo;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + ((tempo == null) ? 0 : tempo.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((tuning == null) ? 0 : tuning.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Song other = (Song) obj;
        if (index != other.index)
            return false;
        if (tempo == null) {
            if (other.tempo != null)
                return false;
        } else if (!tempo.equals(other.tempo))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (tuning != other.tuning)
            return false;
        return true;
    }
}
