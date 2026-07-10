package org.colston.kicks.document;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType()
public abstract class AbstractLocatable implements Locatable {
    @XmlAttribute
    private int index;
    @XmlAttribute
    private int offset;

    protected AbstractLocatable() {
    }

    public AbstractLocatable(int index, int offset) {
        this.index = index;
        this.offset = offset;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void move(int indexDelta, int offsetDelta) {
        this.index += indexDelta;
        this.offset += offsetDelta;
        if (offset > CELL_TICKS) {
            // moved past the end of the cell on to the next cell
            offset = offset % CELL_TICKS;
            index++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractLocatable that)) return false;

        return index == that.index && offset == that.offset;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + offset;
        return result;
    }
}
