package org.colston.kicks.document;

import jakarta.xml.bind.annotation.*;

import java.util.Objects;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class Lyric extends AbstractLocatable {

    @XmlElement
    private String value;

    @SuppressWarnings("unused")
    private Lyric() {
        super();
    }

    public Lyric(int cursorIndex, int cursorOffset, String s) {
        super(cursorIndex, cursorOffset);
        this.value = s;
    }

    public String getValue() {
        return value;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Lyric lyric)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(value, lyric.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(value);
        return result;
    }
}
