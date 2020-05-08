package org.colston.printpdf;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeListener;

public class PDFBoxPrintService implements PrintService
{
	private static final DocFlavor[] supportedDocFlavours = new DocFlavor[] {
			// TODO DocFlavor.SERVICE_FORMATTED.PAGEABLE,
			DocFlavor.SERVICE_FORMATTED.PRINTABLE
	};
	
	@Override
	public String getName()
	{
		return "SC PDFBox Print To PDF";
	}

	@Override
	public DocPrintJob createPrintJob()
	{
		return new PDFBoxPrintJob(this, PDFBoxFontStore.create());
	}

	@Override
	public void addPrintServiceAttributeListener(PrintServiceAttributeListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removePrintServiceAttributeListener(PrintServiceAttributeListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public PrintServiceAttributeSet getAttributes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends PrintServiceAttribute> T getAttribute(Class<T> category)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocFlavor[] getSupportedDocFlavors()
	{
		return supportedDocFlavours;
	}

	@Override
	public boolean isDocFlavorSupported(DocFlavor flavor)
	{
		for (int i = 0; i < supportedDocFlavours.length; i++)
		{
			if (supportedDocFlavours[i].equals(flavor))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<?>[] getSupportedAttributeCategories()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAttributeCategorySupported(Class<? extends Attribute> category)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getDefaultAttributeValue(Class<? extends Attribute> category)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getSupportedAttributeValues(Class<? extends Attribute> category, DocFlavor flavor,
			AttributeSet attributes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAttributeValueSupported(Attribute attrval, DocFlavor flavor, AttributeSet attributes)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AttributeSet getUnsupportedAttributes(DocFlavor flavor, AttributeSet attributes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceUIFactory getServiceUIFactory()
	{
		return null;
	}
}
