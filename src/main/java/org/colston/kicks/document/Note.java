package org.colston.kicks.document;

import jakarta.xml.bind.annotation.*;

import java.util.Objects;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(
        propOrder = {
                "small",
                "accidental",
                "utou",
                "chord",
                "slur",
                "finger"
        })
public class Note implements Locatable {
    @XmlAttribute
    private int index;
    @XmlAttribute
    private int offset;
    @XmlAttribute
    private int string;
    @XmlAttribute
    private int placement;

    @XmlElement(defaultValue = "false")
    private Boolean small = null;
    @XmlElement(defaultValue = "NONE")
    private Accidental accidental = null;
    @XmlElement(defaultValue = "NONE")
    private Utou utou = null;
    @XmlElement(defaultValue = "false")
    private Boolean chord = null;
    @XmlElement(defaultValue = "false")
    private Boolean slur = null;
    @XmlElement(defaultValue = "0")
    private Integer finger = null;

    @SuppressWarnings("unused")
    private Note() {
    }

    public Note(int index, int offset, int string, int placement) {
        this.index = index;
        this.offset = offset;
        this.string = string;
        this.placement = placement;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    public int getString() {
        return string;
    }

    public int getPlacement() {
        return placement;
    }

    public boolean isSmall() {
        return small == null ? Boolean.FALSE : small;
    }

    public void setSmall(boolean small) {
        this.small = small;
    }

    public Accidental getAccidental() {
        return accidental == null ? Accidental.NONE : accidental;
    }

    public void setAccidental(Accidental accidental) {
        this.accidental = accidental;
    }

    public Utou getUtou() {
        return utou == null ? Utou.NONE : utou;
    }

    public void setUtou(Utou utou) {
        this.utou = utou;
    }

    public boolean isChord() {
        return chord == null ? Boolean.FALSE : chord;
    }

    public void setChord(boolean chord) {
        this.chord = chord;
    }

    public boolean isSlur() {
        return slur == null ? Boolean.FALSE : slur;
    }

    public void setSlur(boolean slur) {
        this.slur = slur;
    }

    public int getFinger() {
        return finger == null ? 0 : finger;
    }

    public void setFinger(int finger) {
        this.finger = finger;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return index == note.index && offset == note.offset && string == note.string && placement == note.placement
                && Objects.equals(small, note.small) && accidental == note.accidental && utou == note.utou
                && Objects.equals(chord, note.chord) && Objects.equals(slur, note.slur)
                && Objects.equals(finger, note.finger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, offset, string, placement, small, accidental, utou, chord, slur, finger);
    }

    @Override
    public void move(int indexDelta, int offsetDelta) {
        this.index += indexDelta;
        this.offset += offsetDelta;
    }
}
