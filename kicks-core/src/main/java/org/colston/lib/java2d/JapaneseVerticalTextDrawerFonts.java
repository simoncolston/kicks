package org.colston.lib.java2d;

import java.awt.*;

public class JapaneseVerticalTextDrawerFonts {

    private static final char STANDARD_JAPANESE_CHAR = '中';

    private final Font textFont;
    private final Font furiganaFont;
    private int textCharWidth = 0;
    private int textFontAscent = 0;
    private int textFontHeight = 0;
    private int furiganaCharWidth = 0;
    private int furiganaFontAscent = 0;
    private int furiganaFontHeight = 0;

    private JapaneseVerticalTextDrawerFonts(Font textFont, Font furiganaFont) {
        this.textFont = textFont;
        this.furiganaFont = furiganaFont;
    }

    public static JapaneseVerticalTextDrawerFonts create(Font textFont, Font furiganaFont, Graphics2D g2) {
        JapaneseVerticalTextDrawerFonts fonts = new JapaneseVerticalTextDrawerFonts(textFont, furiganaFont);
        fonts.analyse(g2);
        return fonts;
    }

    private void analyse(Graphics2D g2) {
        g2.setFont(textFont);
        FontMetrics textFontMetrics = g2.getFontMetrics();
        textFontAscent = textFontMetrics.getAscent();
        textCharWidth = textFontMetrics.charWidth(STANDARD_JAPANESE_CHAR);
        textFontHeight = textFontMetrics.getHeight();
        g2.setFont(furiganaFont);
        FontMetrics furiganaFontMetrics = g2.getFontMetrics();
        furiganaFontAscent = furiganaFontMetrics.getAscent();
        furiganaCharWidth = furiganaFontMetrics.charWidth(STANDARD_JAPANESE_CHAR);
        furiganaFontHeight = furiganaFontMetrics.getHeight();
    }

    public Font getTextFont() {
        return textFont;
    }

    public Font getFuriganaFont() {
        return furiganaFont;
    }

    public int getTextCharWidth() {
        return textCharWidth;
    }

    public int getTextFontAscent() {
        return textFontAscent;
    }

    public int getTextFontHeight() {
        return textFontHeight;
    }

    public int getFuriganaCharWidth() {
        return furiganaCharWidth;
    }

    public int getFuriganaFontAscent() {
        return furiganaFontAscent;
    }

    public int getFuriganaFontHeight() {
        return furiganaFontHeight;
    }
}
