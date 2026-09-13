package org.colston.kicks.render;

import org.colston.kicks.document.Note;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.MediaSize;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

class FontNoteKanjiRendererTest {

    public static final String FONT_NAME_1 = "EPSON 教科書体Ｍ V";

    @Test
    void render() throws IOException, FontFormatException {
        GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (String resourceName : PageRenderer.getFontResourceNames()) {
            try (BufferedInputStream bis = new BufferedInputStream(Objects.requireNonNull(PageRenderer.class.getResourceAsStream(resourceName)))) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, bis);
                graphics.registerFont(f);
            }
        }

        int height = (int) (MediaSize.ISO.A4.getX(Size2DSyntax.INCH) * 72.0);
        int width = (int) (MediaSize.ISO.A4.getY(Size2DSyntax.INCH) * 72.0);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font(FONT_NAME_1, Font.PLAIN, 26));

        FontNoteKanjiRenderer renderer = new FontNoteKanjiRenderer();

        Note n = new Note(0, 6, 1, 0);
        renderer.render(g2, n, 100, 100);

        n = new Note(0, 6, 1, 4);
        renderer.render(g2, n, 150, 100);

        n = new Note(0, 6, 1, 5);
        renderer.render(g2, n, 200, 100);

        g2.dispose();

        ImageIO.write(img, "png", new File("target/FontNoteKanjiRendererTest.png"));
    }
}