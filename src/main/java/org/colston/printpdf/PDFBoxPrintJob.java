package org.colston.printpdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.util.Matrix;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobListener;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

public class PDFBoxPrintJob implements DocPrintJob {
    private final PDFBoxPrintService service;

    private String jobName = "Print PDF";
    private MediaSize mediaSize = MediaSize.ISO.A4;
    private MediaPrintableArea mediaPrintableArea = null;
    private OrientationRequested orientation = OrientationRequested.PORTRAIT;
    private File outputFile = null;

    private final PDFBoxFontStore fontStore;
    private PDFBoxPrintFontMap addedFonts = null;

    PDFBoxPrintJob(PDFBoxPrintService service, PDFBoxFontStore fontStore) {
        this.service = service;
        this.fontStore = fontStore;
    }

    @Override
    public PrintService getPrintService() {
        return service;
    }

    @Override
    public PrintJobAttributeSet getAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addPrintJobListener(PrintJobListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removePrintJobListener(PrintJobListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addPrintJobAttributeListener(PrintJobAttributeListener listener, PrintJobAttributeSet attributes) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removePrintJobAttributeListener(PrintJobAttributeListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void print(Doc doc, PrintRequestAttributeSet pras) throws PrintException {
        DocFlavor flavour = doc.getDocFlavor();
        if (flavour == null || (!service.isDocFlavorSupported(flavour))) {
//			TODO: notifyEvent(PrintJobEvent.JOB_FAILED);
            throw new PrintException("Invalid document flavour: " + flavour);
        }

        Object data;
        try {
            data = doc.getPrintData();
        } catch (IOException e) {
//			TODO notifyEvent(PrintJobEvent.JOB_FAILED);
            throw new PrintException("Print data not available: " + e.toString());
        }
        if (data == null) {
//			TODO notifyEvent(PrintJobEvent.JOB_FAILED);
            throw new PrintException("Print data is null!");
        }

        //Decode the attributes: priority goes: doc > request (> job?)
        //Start by consolidating them... then, decode the one's we need
        PrintRequestAttributeSet aset = consolidateAttributes(doc.getAttributes(), pras);
        decodeAttributes(doc, aset);

        try {
            String className = flavour.getRepresentationClassName();
            if ("java.awt.print.Printable".contentEquals(className)) {
                printPrintable((Printable) data);
            }
            //TODO:
//		else if ("java.awt.print.Pageable".equals(className))
//		{
//			printPageable((Pageable) data);
//		}
        } catch (PrinterException e) {
            throw new PrintException(e);
        }
    }

    private void printPrintable(Printable printable) throws PrinterException {
        PageFormat pageFormat = new PageFormat();

        Paper paper = new Paper();
        paper.setSize(mediaSize.getX(Size2DSyntax.INCH) * 72.0, mediaSize.getY(Size2DSyntax.INCH) * 72.0);
        float[] ia;
        if (mediaPrintableArea != null) {
            ia = mediaPrintableArea.getPrintableArea(MediaPrintableArea.INCH);
            for (int i = 0; i < ia.length; i++) {
                ia[i] = ia[i] * 72f;
            }
        } else {
            ia = new float[]{0, 0, (float) paper.getWidth(), (float) paper.getHeight()};
        }
        paper.setImageableArea(ia[0], ia[1], ia[2], ia[3]);
        pageFormat.setPaper(paper);

        if (orientation == OrientationRequested.LANDSCAPE) {
            pageFormat.setOrientation(PageFormat.LANDSCAPE);
        }

        try {
            try (PDDocument doc = new PDDocument()) {
                //load the additional fonts and attach to the document for embedding
                if (addedFonts != null) {
                    for (PDFBoxPrintFontMap.Mapping m : addedFonts) {
                        try (BufferedInputStream bis = new BufferedInputStream(
                                Objects.requireNonNull(m.getCls().getResourceAsStream(m.getFontResourceName())))) {
                            PDFont pdfont = PDType0Font.load(doc, bis);
                            fontStore.put(m.getFont(), pdfont);
                        }
                    }
                }

                PDPage page = new PDPage(new PDRectangle((float) paper.getWidth(), (float) paper.getHeight()));
                if (orientation == OrientationRequested.LANDSCAPE) {
                    page.setRotation(90);
                }
                doc.addPage(page);

                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    if (orientation == OrientationRequested.LANDSCAPE) {
                        //rotate to landscape - origin is now top-left so just always negate y
                        Matrix landscape = Matrix.getRotateInstance(Math.PI / 2, 0, 0);
                        cs.transform(landscape);
                    } else {
                        //move origin to top-left then always negate y
                        Matrix portrait = Matrix.getTranslateInstance(0, (float) paper.getHeight());
                        cs.transform(portrait);
                    }

                    Graphics2D graphics = new PDFBoxGraphics2D(cs, fontStore);
                    int pageIndex = 0;
                    printable.print(graphics, pageFormat, pageIndex);
                }
                doc.save(outputFile);
            }
        } catch (IOException e) {
            throw new PrinterException("PDFPrint job: " + jobName + "Error writing PDF file" + e.getMessage());
        }
    }

    private void decodeAttributes(Doc doc, PrintRequestAttributeSet aset) throws PrintException {
        Attribute[] attrs = aset.toArray();
        for (Attribute attr : attrs) {
            Class<?> category = attr.getCategory();
            if (category == Destination.class) {
                URI uri = ((Destination) attr).getURI();
                if (uri == null || !"file".equals(uri.getScheme())) {
//					TODO notifyEvent(PrintJobEvent.JOB_FAILED);
                    throw new PrintException("Destination attribute is not a 'file:' URI");
                }
                try {
                    outputFile = new File(uri);
                } catch (Exception e) {
//						TODO notifyEvent(PrintJobEvent.JOB_FAILED);
                    throw new PrintException(e);
                }
                // check write access
                SecurityManager security = System.getSecurityManager();
                if (security != null) {
                    try {
                        security.checkWrite(outputFile.getPath());
                    } catch (SecurityException se) {
//							TODO notifyEvent(PrintJobEvent.JOB_FAILED);
                        throw new PrintException(se);
                    }
                }
            } else if (category == JobName.class) {
                jobName = ((JobName) attr).getValue();
            } else if (category == Media.class) {
                if (attr instanceof MediaSizeName) {
                    MediaSizeName mn = (MediaSizeName) attr;
                    MediaSize ms = MediaSize.getMediaSizeForName(mn);
                    if (ms != null) {
                        mediaSize = ms;
                    }
                }
            } else if (category == MediaPrintableArea.class) {
                mediaPrintableArea = (MediaPrintableArea) attr;
            } else if (category == OrientationRequested.class) {
                orientation = (OrientationRequested) attr;
            } else if (category == PDFBoxPrintFontMap.class) {
                addedFonts = (PDFBoxPrintFontMap) attr;
            }
        }

        //Validation
        //MUST have an output file name
        if (outputFile == null) {
//			TODO notifyEvent(PrintJobEvent.JOB_FAILED);
            throw new PrintException("Must specify a file in the Destination attribute");
        }
    }

    private PrintRequestAttributeSet consolidateAttributes(DocAttributeSet das, PrintRequestAttributeSet pras) {
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        if (pras != null) {
            aset.addAll(pras);
        }
        if (das != null) {
            Attribute[] attrs = das.toArray();
            for (Attribute attr : attrs) {
                if (attr instanceof PrintRequestAttribute) {
                    aset.add(attr);
                }
            }
        }
        return aset;
    }
}
