package org.colston.kicks.document;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.Objects;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(
        propOrder = {
                "start"
        })
public class Phrase extends AbstractLocatable {

    @XmlElement
    private Boolean start = null;

    @SuppressWarnings("unused")
    private Phrase() {
        super();
    }

    public Phrase(int index, int offset, boolean start) {
        super(index, offset);
        this.start = start;
    }

    public boolean isStart() {
        return start == null ? Boolean.TRUE : start;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Phrase phrase)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(start, phrase.start);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(start);
        return result;
    }
}
