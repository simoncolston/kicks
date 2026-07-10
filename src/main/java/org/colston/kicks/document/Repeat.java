package org.colston.kicks.document;

import jakarta.xml.bind.annotation.*;

import java.util.Objects;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(
        propOrder = {
                "back",
                "style"
        })
public class Repeat extends AbstractLocatable {

    @XmlElement
    private Boolean back = null;
    @XmlElement
    private RepeatStyle style;

    @SuppressWarnings("unused")
    private Repeat() {
        super();
    }

    public Repeat(int index, int offset, boolean back) {
        super(offset, index);
        this.back = back;
    }

    public boolean isBack() {
        return back == null ? Boolean.FALSE : back;
    }

    public RepeatStyle getStyle() {
        return style == null ? RepeatStyle.TRIANGLE_FILLED : style;
    }

    public void setStyle(RepeatStyle style) {
        this.style = style;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Repeat repeat)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(back, repeat.back) && style == repeat.style;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(back);
        result = 31 * result + Objects.hashCode(style);
        return result;
    }
}
