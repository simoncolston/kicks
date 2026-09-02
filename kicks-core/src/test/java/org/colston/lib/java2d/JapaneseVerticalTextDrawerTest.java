package org.colston.lib.java2d;

import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.persistence.DocumentStoreFactory;
import org.colston.kicks.render.PageRenderer;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.Objects;

class JapaneseVerticalTextDrawerTest {

    public static final String FONT_NAME_1 = "EPSON 教科書体Ｍ V";
    public static final String FONT_NAME_2 = "IPAMincho V";

    @Test
    void draw() throws Exception {

        GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (String resourceName : PageRenderer.getFontResourceNames()) {
            try (BufferedInputStream bis = new BufferedInputStream(Objects.requireNonNull(PageRenderer.class.getResourceAsStream(resourceName)))) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, bis);
                graphics.registerFont(f);
            }
        }
        try (BufferedInputStream bis = new BufferedInputStream(Objects.requireNonNull(PageRenderer.class.getResourceAsStream("ipamv.ttf")))) {
            Font f = Font.createFont(Font.TRUETYPE_FONT, bis);
            graphics.registerFont(f);
        }

        int height = (int) (MediaSize.ISO.A4.getX(Size2DSyntax.INCH) * 72.0);
        int width = (int) (MediaSize.ISO.A4.getY(Size2DSyntax.INCH) * 72.0);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.BLACK);

        JapaneseVerticalTextDrawerFonts fonts1 = JapaneseVerticalTextDrawerFonts.create(
                new Font(FONT_NAME_1, Font.BOLD, 26),
                new Font(FONT_NAME_1, Font.BOLD, 12),
                g2);
        JapaneseVerticalTextDrawerFonts fonts2 = JapaneseVerticalTextDrawerFonts.create(
                new Font(FONT_NAME_2, Font.PLAIN, 26),
                new Font(FONT_NAME_2, Font.PLAIN, 12),
                g2);
        JapaneseVerticalTextDrawer vtext1 = JapaneseVerticalTextDrawer.create(fonts1)
                .withTextToFuriganaSpacingAdjustment(0);
        JapaneseVerticalTextDrawer vtext2 = JapaneseVerticalTextDrawer.create(fonts2)
                .withTextToFuriganaSpacingAdjustment(0);

        vtext1.draw(g2, "{日本語}{にほんご}の{情}{なさけ}とう{肝}{ちむ}ドンドン", 10, 10);
        vtext1.draw(g2, "{日本語}{にほんご}{新城}{あらぐすく}の", 50, 10);
        vtext2.draw(g2, "{日本語}{にほんご}の{情}{なさけ}とう{肝}{ちむ}ドンドン", 110, 10);
        vtext2.draw(g2, "{日本語}{にほんご}{新城}{あらぐすく}の", 150, 10);

//        File inputFile = new File("testdata/" + "asadoyayunta" + ".kicks");
//        KicksDocument doc = DocumentStoreFactory.createDefault().load(inputFile);
//        PageRenderer renderer = PageRenderer.create(doc);
//        renderer.doPaint(g2);


        g2.dispose();

        ImageIO.write(img, "png", new File("/home/simon/tmp/test.png"));
    }
}