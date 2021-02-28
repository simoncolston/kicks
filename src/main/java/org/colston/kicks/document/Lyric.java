package org.colston.kicks.document;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class Lyric implements Locatable {
    @XmlAttribute
    private int index;
    @XmlAttribute
    private int offset;

    @XmlElement
    private String value;

    @SuppressWarnings("unused")
    private Lyric() {
    }

    public Lyric(int cursorIndex, int cursorOffset, String s) {
        this.index = cursorIndex;
        this.offset = cursorOffset;
        this.value = s;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + offset;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        Lyric other = (Lyric) obj;
        if (index != other.index)
            return false;
        if (offset != other.offset)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}
