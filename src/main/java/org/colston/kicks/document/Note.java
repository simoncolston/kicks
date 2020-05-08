package org.colston.kicks.document;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(
propOrder = {
		"small",
		"accidental",
		"utou",
		"chord",
		"slur"
})
public class Note implements Locatable
{
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
	
	@SuppressWarnings("unused")
	private Note()
	{
	}
	
	public Note(int index, int offset, int string, int placement)
	{
		this.index = index;
		this.offset = offset;
		this.string = string;
		this.placement = placement;
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
	
	public int getString()
	{
		return string;
	}
	
	public int getPlacement()
	{
		return placement;
	}

	public boolean isSmall()
	{
		return small == null ? Boolean.FALSE : small;
	}

	public void setSmall(boolean small)
	{
		this.small = small;
	}

	public Accidental getAccidental()
	{
		return accidental == null ? Accidental.NONE : accidental;
	}

	public void setAccidental(Accidental accidental)
	{
		this.accidental = accidental;
	}

	public Utou getUtou()
	{
		return utou == null ? Utou.NONE : utou;
	}

	public void setUtou(Utou utou)
	{
		this.utou = utou;
	}

	public boolean isChord()
	{
		return chord == null ? Boolean.FALSE : chord;
	}

	public void setChord(boolean chord)
	{
		this.chord = chord;
	}

	public boolean isSlur()
	{
		return slur == null ? Boolean.FALSE : slur;
	}

	public void setSlur(boolean slur)
	{
		this.slur = slur;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accidental == null) ? 0 : accidental.hashCode());
		result = prime * result + ((chord == null) ? 0 : chord.hashCode());
		result = prime * result + index;
		result = prime * result + offset;
		result = prime * result + placement;
		result = prime * result + ((slur == null) ? 0 : slur.hashCode());
		result = prime * result + ((small == null) ? 0 : small.hashCode());
		result = prime * result + string;
		result = prime * result + ((utou == null) ? 0 : utou.hashCode());
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
		Note other = (Note) obj;
		if (accidental != other.accidental)
			return false;
		if (chord == null)
		{
			if (other.chord != null)
				return false;
		}
		else if (!chord.equals(other.chord))
			return false;
		if (index != other.index)
			return false;
		if (offset != other.offset)
			return false;
		if (placement != other.placement)
			return false;
		if (slur == null)
		{
			if (other.slur != null)
				return false;
		}
		else if (!slur.equals(other.slur))
			return false;
		if (small == null)
		{
			if (other.small != null)
				return false;
		}
		else if (!small.equals(other.small))
			return false;
		if (string != other.string)
			return false;
		if (utou != other.utou)
			return false;
		return true;
	}
}
