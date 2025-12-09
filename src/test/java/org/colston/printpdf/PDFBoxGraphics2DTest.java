package org.colston.printpdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;
import org.junit.jupiter.api.Test;

import javax.print.attribute.Size2DSyntax;
import java.awt.*;
import java.awt.print.Paper;
import java.io.IOException;

import static org.colston.printpdf.PDFBoxPrintJob.DEFAULT_MEDIA_SIZE;

class PDFBoxGraphics2DTest {

    @Test
    void drawString() throws IOException {
        PDFBoxFontStore fontStore = PDFBoxFontStore.create();
        Paper paper = new Paper();
        paper.setSize(DEFAULT_MEDIA_SIZE.getX(Size2DSyntax.INCH) * 72.0, DEFAULT_MEDIA_SIZE.getY(Size2DSyntax.INCH) * 72.0);
        float[] ia = new float[]{0, 0, (float) paper.getWidth(), (float) paper.getHeight()};
        paper.setImageableArea(ia[0], ia[1], ia[2], ia[3]);

        try (PDDocument doc = new PDDocument()) {

            PDPage page = new PDPage(new PDRectangle((float) paper.getWidth(), (float) paper.getHeight()));
            page.setRotation(90);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                //rotate to landscape - origin is now top-left so just always negate y
                Matrix landscape = Matrix.getRotateInstance(Math.PI / 2, 0, 0);
                cs.transform(landscape);

                Graphics2D graphics = new PDFBoxGraphics2D(cs, fontStore);
                // set a border
                graphics.translate(20, 30);
                drawStuff(graphics, 10, 0);

                // draw with new graphics
                Graphics2D g2 = (Graphics2D) graphics.create();
                drawStuff(g2, 210, 0);
                g2.dispose();

                // re-draw with old graphics
                drawStuff(graphics, 420, 0);
                graphics.dispose();
            }

            doc.save("target/test.pdf");
        }
    }

    private void drawStuff(Graphics2D graphics, int baseX, int baseY) {
        graphics.setColor(Color.BLACK);
        graphics.drawString("Hello, World!", baseX, baseY);
        graphics.setColor(Color.BLUE);
        graphics.drawString("Hello, World!", baseX, baseY + 100);
        graphics.drawLine(baseX + 80, baseY, baseX + 80, baseY + 100);
        graphics.setColor(Color.GREEN);

        int[] xs = new int[3];
        int[] ys = new int[3];
        xs[0] = baseX + 10;
        ys[0] = baseY + 30;
        xs[1] = baseX;
        ys[1] = baseY + 50;
        xs[2] = baseX + 20;
        ys[2] = baseY + 50;
        Polygon tri = new Polygon(xs, ys, 3);
        graphics.fill(tri);

        graphics.drawString("Hello, World!", baseX + 110, baseY + 50);
    }
}