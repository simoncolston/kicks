package org.colston.kicks.document;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(
        propOrder = {
                "type"
        })
public class Break {
    @XmlAttribute
    private int index;

    @XmlElement
    private BreakType type;

    @SuppressWarnings("unused")
    private Break() {
    }

    public Break(int index, BreakType type) {
        this.index = index;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public BreakType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        Break other = (Break) obj;
        if (index != other.index)
            return false;
        return type == other.type;
    }
}
