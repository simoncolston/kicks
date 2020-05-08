package org.colston.printpdf;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintRequestAttribute;

public class PDFBoxPrintFontMap implements PrintRequestAttribute, DocAttribute, Iterable<PDFBoxPrintFontMap.Mapping>
{
	public class Mapping
	{
		private Font font;
		private Class<?> cls;
		private String fontResourceName;

		Mapping(Font font, Class<?> cls, String fontResourceName)
		{
			this.font = font;
			this.cls = cls;
			this.fontResourceName = fontResourceName;
		}

		public Font getFont()
		{
			return font;
		}

		public Class<?> getCls()
		{
			return cls;
		}

		public String getFontResourceName()
		{
			return fontResourceName;
		}
	}
	
	private List<Mapping> list = new ArrayList<>();
	
	@Override
	public Class<? extends Attribute> getCategory()
	{
		return PDFBoxPrintFontMap.class;
	}

	@Override
	public String getName()
	{
		return "pdfbox-print-font-map";
	}

	public void add(Font font, Class<?> cls, String fontResourceName)
	{
		list.add(new Mapping(font, cls, fontResourceName));
	}
	
	@Override
	public Iterator<Mapping> iterator()
	{
		return list.iterator();
	}
}
