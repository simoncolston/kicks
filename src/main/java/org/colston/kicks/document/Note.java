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
public class Note extends AbstractLocatable {
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
        super();
    }

    public Note(int index, int offset, int string, int placement) {
        super(index, offset);
        this.string = string;
        this.placement = placement;
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
    public final boolean equals(Object o) {
        if (!(o instanceof Note note)) return false;

        return super.equals(o)
                && string == note.string && placement == note.placement && Objects.equals(small, note.small)
                && accidental == note.accidental && utou == note.utou && Objects.equals(chord, note.chord)
                && Objects.equals(slur, note.slur) && Objects.equals(finger, note.finger);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + string;
        result = 31 * result + placement;
        result = 31 * result + Objects.hashCode(small);
        result = 31 * result + Objects.hashCode(accidental);
        result = 31 * result + Objects.hashCode(utou);
        result = 31 * result + Objects.hashCode(chord);
        result = 31 * result + Objects.hashCode(slur);
        result = 31 * result + Objects.hashCode(finger);
        return result;
    }
}
