package org.colston.printpdf;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.OrientationRequested;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PDFBoxDocumentCreator {

    private static final double MM_IN_INCH = 25.4f;

    private MediaSize mediaSize = MediaSize.ISO.A4;
    private int margin = 20; // millimetres
    private OrientationRequested orientation = OrientationRequested.PORTRAIT;
    private boolean asImage = false; // minimum canvas size to render the image

    private PDFBoxDocumentCreator() {
    }

    public static PDFBoxDocumentCreator getInstance() {
        return new PDFBoxDocumentCreator();
    }

    public PDFBoxDocumentCreator mediaSize(MediaSize mediaSize) {
        this.mediaSize = mediaSize;
        return this;
    }

    public PDFBoxDocumentCreator margin(int margin) {
        this.margin = margin;
        return this;
    }

    public PDFBoxDocumentCreator orientation(OrientationRequested orientation) {
        this.orientation = orientation;
        return this;
    }

    public PDFBoxDocumentCreator asImage(boolean asImage) {
        this.asImage = asImage;
        return this;
    }

    public void save(PDFBoxDocumentRenderer renderer, File destination) throws IOException {

        renderer.asImage(asImage);

        try (PDDocument doc = new PDDocument()) {
            for (int pageIndex = 0; pageIndex < renderer.getNumberOfPages(); pageIndex++) {
                addPage(renderer, pageIndex, doc);
            }
            doc.save(destination);
        }
    }

    private void addPage(PDFBoxDocumentRenderer renderer, int pageIndex, PDDocument doc) throws IOException {
        PDRectangle mediaBox = getMediaBox(renderer, pageIndex);
        PDPage page = new PDPage(mediaBox);
        if (!asImage && orientation == OrientationRequested.LANDSCAPE) {
            page.setRotation(90);
        }
        doc.addPage(page);

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            if (!asImage && orientation == OrientationRequested.LANDSCAPE) {
                //rotate to landscape - origin is now top-left so just always negate y
                Matrix landscape = Matrix.getRotateInstance(Math.PI / 2, 0, 0);
                cs.transform(landscape);
            } else {
                //move origin to top-left then always negate y
                Matrix portrait = Matrix.getTranslateInstance(0, mediaBox.getHeight());
                cs.transform(portrait);
            }

            Graphics2D graphics = new PDFBoxGraphics2D(
                    cs,
                    PDFBoxFontStore.create().loadFontMap(doc, renderer.getFontMap()),
                    new LayerUtility(doc));

            if (!asImage) {
                // translate and scale to render inside the margins and at maximum size without skewing
                double marginInPoints = (margin / MM_IN_INCH) * 72;
                graphics.translate(marginInPoints, marginInPoints);
                double scale = getScale(renderer, pageIndex, mediaBox, marginInPoints);
                graphics.scale(scale, scale);
            }

            renderer.render(graphics, pageIndex);
            graphics.dispose();
        }
    }

    private PDRectangle getMediaBox(PDFBoxDocumentRenderer renderer, int pageIndex) {
        if (asImage) {
            return new PDRectangle(renderer.getWidth(pageIndex), renderer.getHeight(pageIndex));
        }
        return new PDRectangle(
                (float) (mediaSize.getX(Size2DSyntax.INCH) * 72.0),
                (float) (mediaSize.getY(Size2DSyntax.INCH) * 72.0));
    }

    private double getScale(PDFBoxDocumentRenderer renderer, int pageIndex, PDRectangle mediaBox, double marginInPoints) {
        PDRectangle orientationBox =
                orientation == OrientationRequested.LANDSCAPE
                        ? new PDRectangle(mediaBox.getHeight(), mediaBox.getWidth())
                        : mediaBox;
        double widthScale = (orientationBox.getWidth() - 2 * marginInPoints) / renderer.getWidth(pageIndex);
        double heightScale = (orientationBox.getHeight() - 2 * marginInPoints) / renderer.getHeight(pageIndex);
        return Math.min(widthScale, heightScale);
    }
}
