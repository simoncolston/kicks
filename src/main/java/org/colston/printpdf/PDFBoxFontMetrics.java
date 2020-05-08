package org.colston.printpdf;

import java.awt.Font;
import java.awt.FontMetrics;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDFont;

public class PDFBoxFontMetrics extends FontMetrics
{

	private PDFont pdfont;

	protected PDFBoxFontMetrics(Font font, PDFont pdfont)
	{
		super(font);
		this.pdfont = pdfont;
	}

	@Override
	public int getHeight()
	{
		return (int) (pdfont.getFontDescriptor().getFontBoundingBox().getHeight() * getFont().getSize());
	}

	
	@Override
	public int getAscent()
	{
		return getHeight();
	}

	
	@Override
	public int getMaxAdvance()
	{
		return stringWidth("中");
	}

	@Override
	public int charWidth(char ch)
	{
        char data[] = {ch};
		return stringWidth(new String(data, 0, 1));
	}

	@Override
	public int stringWidth(String str)
	{
		try
		{
			return (int) pdfont.getStringWidth(str) * getFont().getSize() / 1000;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return 1;
	}
}
