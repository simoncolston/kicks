package org.colston.kicks.document;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(
propOrder = {
		"back",
		"style"
})
public class Repeat implements Locatable
{
	@XmlAttribute
	private int index;
	@XmlAttribute
	private int offset;
	
	@XmlElement
	private Boolean back = null;
	@XmlElement
	private RepeatStyle style;
	
	@SuppressWarnings("unused")
	private Repeat()
	{
	}
	
	public Repeat(int index, int offset, boolean back)
	{
		this.index = index;
		this.offset = offset;
		this.back = back;
	}
	
	@Override
	public int getIndex()
	{
		return index;
	}

	@Override
	public int getOffset()
	{
		return offset;
	}

	public boolean isBack()
	{
		return back == null ? Boolean.FALSE : back;
	}

	public RepeatStyle getStyle()
	{
		return style == null ? RepeatStyle.TRIANGLE_FILLED : style;
	}

	public void setStyle(RepeatStyle style)
	{
		this.style = style;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((back == null) ? 0 : back.hashCode());
		result = prime * result + index;
		result = prime * result + offset;
		result = prime * result + ((style == null) ? 0 : style.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Repeat other = (Repeat) obj;
		if (back == null)
		{
			if (other.back != null)
				return false;
		}
		else if (!back.equals(other.back))
			return false;
		if (index != other.index)
			return false;
		if (offset != other.offset)
			return false;
		if (style != other.style)
			return false;
		return true;
	}
}
