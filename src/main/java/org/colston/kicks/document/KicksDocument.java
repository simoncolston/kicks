package org.colston.kicks.document;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The bean that contains the whole document and defines how it is persisted as XML.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "kunkunshi")
@XmlType(
        propOrder =
                {
                        "properties",
                        "songs",
                        "notes",
                        "breaks",
                        "repeats",
                        "lyrics"
                })
public class KicksDocument {

    @XmlAttribute(required = true)
    private final int version = 1;

    @XmlElement(required = true)
    private final Properties properties = new Properties();

    @XmlElementWrapper(name = "songs", required = true)
    @XmlElement(name = "song")
    private final List<Song> songs = new ArrayList<>();

    @XmlElementWrapper(name = "notes")
    @XmlElement(name = "note")
    private final List<Note> notes = new ArrayList<>();

    @XmlElementWrapper(name = "breaks")
    @XmlElement(name = "break")
    private final List<Break> breaks = new ArrayList<>();

    @XmlElementWrapper(name = "repeats")
    @XmlElement(name = "repeat")
    private final List<Repeat> repeats = new ArrayList<>();

    @XmlElementWrapper(name = "lyrics")
    @XmlElement(name = "lyric")
    private final List<Lyric> lyrics = new ArrayList<>();

    public KicksDocument() {
        songs.add(new Song(0));
    }

    public Properties getProperties() {
        return properties;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public Note getNote(int index, int offset) {
        if (notes.isEmpty()) {
            return null;
        }
        int listIndex = Collections.binarySearch(notes, new SimpleLocatable(index, offset),
                LocatableComparator.INSTANCE);
        return listIndex >= 0 ? notes.get(listIndex) : null;
    }

    public List<Break> getBreaks() {
        return breaks;
    }

    public List<Repeat> getRepeats() {
        return repeats;
    }

    public List<Lyric> getLyrics() {
        return lyrics;
    }

    public Lyric getLyric(int index, int offset) {
        int off = Collections.binarySearch(lyrics, new SimpleLocatable(index, offset), LocatableComparator.INSTANCE);
        return off >= 0 ? lyrics.get(off) : null;
    }

    public String getTitle() {
        Song song = songs.getFirst();
        return song.getTitle();
    }

    public Tuning getTuning() {
        return songs.getFirst().getTuning();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + breaks.hashCode();
        result = prime * result + lyrics.hashCode();
        result = prime * result + notes.hashCode();
        result = prime * result + properties.hashCode();
        result = prime * result + repeats.hashCode();
        result = prime * result + songs.hashCode();
        result = prime * result + version;
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
        KicksDocument other = (KicksDocument) obj;
        if (!breaks.equals(other.breaks))
            return false;
        if (!lyrics.equals(other.lyrics))
            return false;
        if (!notes.equals(other.notes))
            return false;
        if (!properties.equals(other.properties))
            return false;
        if (!repeats.equals(other.repeats))
            return false;
        return songs.equals(other.songs);
    }

}
