package org.colston.printpdf;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;
import org.colston.kicks.render.PageRenderer;
import org.junit.jupiter.api.Test;

import javax.print.attribute.Size2DSyntax;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
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
                // for portrait - origin is bottom left so translate up to top-left
//                Matrix m = Matrix.getTranslateInstance(0, (float) paper.getHeight());
//                cs.transform(m);

                Graphics2D graphics = new PDFBoxGraphics2D(cs, fontStore, new LayerUtility(doc));
                // set a border
                graphics.translate(20, 30);

                Image bi = createBufferedImage();
                graphics.drawImage(bi, 200, 400, null);


                graphics.setFont(new Font("Serif", Font.PLAIN, 12));
                drawStuff(graphics, 10, 0);

                // draw with new graphics
                Graphics2D g2 = (Graphics2D) graphics.create();
                graphics.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 14));
                drawStuff(g2, 210, 0);
                g2.dispose();

                // re-draw with old graphics
                drawStuff(graphics, 420, 0);

                // rotate test
                graphics.drawString("Rotate test", 10, 200);

                g2 = (Graphics2D) graphics.create();
                g2.rotate(Math.toRadians(90), 100, 200);
                g2.drawString("Rotate test", 100, 200);
                g2.dispose();

                graphics.drawString("Rotate test", 10, 220);

                PDFBoxResourceImage image = new PDFBoxResourceImage(PageRenderer.class, "note_ai.pdf");
                graphics.drawImage(image, 10, 300, null);
                graphics.drawRect(10, 300 - 36, 24 + 2, 36);
                graphics.drawLine(5, 300, 15, 300);
                graphics.drawLine(10, 290, 10, 310);

                PDFBoxResourceImage image2 = new PDFBoxResourceImage(PageRenderer.class, "note_yon.pdf");
                graphics.drawImage(image2, 10, 340, null);
                PDFBoxResourceImage image3 = new PDFBoxResourceImage(PageRenderer.class, "note_ai.pdf");
                image3.setTransform(AffineTransform.getRotateInstance(Math.toRadians(-90)));
                graphics.drawImage(image3, 10, 380, null);

                g2 = (Graphics2D) graphics.create();
                g2.translate(0, image3.getWidth(null));
                g2.rotate(Math.toRadians(-90), 10, 380);
                g2.drawRect(10, 380, image3.getWidth(null), image3.getHeight(null));
                g2.dispose();

                graphics.dispose();
            }

            doc.save("target/test.pdf");
        }
    }

    private Image createBufferedImage() {
        BufferedImage image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);
        g2d.setColor(Color.CYAN.darker());
        g2d.fillOval(40, 90, 20, 20);
        g2d.setColor(Color.RED.darker());
        g2d.drawOval(40, 90, 20, 20);
        g2d.dispose();
        return image;
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

        graphics.setColor(Color.RED.darker());
        graphics.draw(tri);

        graphics.setColor(Color.GREEN.darker());
        Shape circle = new Arc2D.Float(baseX + 30f, baseY + 30f, 20f, 20f, 0f, 360f, Arc2D.OPEN);
        graphics.draw(circle);

        graphics.setColor(Color.BLUE.darker());
        graphics.drawOval(baseX + 55, baseY + 30, 20, 30);

        graphics.setColor(Color.PINK.darker());
        graphics.fillOval(baseX + 30, baseY + 55, 20, 20);

        graphics.setColor(Color.RED.darker());
        graphics.drawString("Hello, World!", baseX + 90, baseY + 50);
    }
}