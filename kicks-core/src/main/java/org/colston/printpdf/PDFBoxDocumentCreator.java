package org.colston.printpdf;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PDFBoxDocumentCreator {

    public void save(PDFBoxDocumentRenderer renderer, File destination) throws IOException {
        try (PDDocument doc = new PDDocument()) {

            PDPage page = new PDPage(new PDRectangle(renderer.getWidth(), renderer.getHeight()));
//            page.setRotation(90);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                //rotate to landscape - origin is now top-left so just always negate y
//                Matrix landscape = Matrix.getRotateInstance(Math.PI / 2, 0, 0);
//                cs.transform(landscape);
                // for portrait - origin is bottom left so translate up to top-left
                Matrix m = Matrix.getTranslateInstance(0, renderer.getHeight());
                cs.transform(m);

                Graphics2D graphics = new PDFBoxGraphics2D(
                        cs,
                        PDFBoxFontStore.create().loadFontMap(doc, renderer.getFontMap()),
                        new LayerUtility(doc));
                renderer.render(graphics);
                graphics.dispose();
            }
            doc.save(destination);
        }
    }
}
