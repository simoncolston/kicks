package org.colston.lib.java2d;

import java.awt.*;

public class JapaneseVerticalTextDrawer {

    private final JapaneseVerticalTextDrawerFonts fonts;
    private int textToFuriganaSpacingAdjustment = -1;

    private int kstart;
    private int kend;
    private int fstart;
    private int fend;

    private JapaneseVerticalTextDrawer(JapaneseVerticalTextDrawerFonts fonts) {
        this.fonts = fonts;
    }

    public static JapaneseVerticalTextDrawer create(JapaneseVerticalTextDrawerFonts fonts) {
        return new JapaneseVerticalTextDrawer(fonts);
    }

    public JapaneseVerticalTextDrawer withTextToFuriganaSpacingAdjustment(int textToFuriganaSpacingAdjustment) {
        this.textToFuriganaSpacingAdjustment = textToFuriganaSpacingAdjustment;
        return this;
    }

    public JapaneseVerticalTextDrawerFonts getFonts() {
        return fonts;
    }

    public void draw(Graphics2D graphics2D, String text, int x, int y) {

        Graphics2D g2 = (Graphics2D) graphics2D.create();

        char[] chars = text.toCharArray();

        g2.setFont(fonts.getTextFont());
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '{') {
                if (!parseFurigana(chars, i)) {
                    // invalid syntax - just draw the char and continue
                    g2.drawChars(chars, i, 1, x, y + fonts.getTextFontAscent());
                    y += fonts.getTextFontHeight();
                    continue;
                }

                // Height of each is number of chars x character size
                int kheight = (kend - kstart) * fonts.getTextFontHeight();
                int fheight = (fend - fstart) * fonts.getFuriganaFontHeight();
                int kpad = 0;
                int fpad = 0;

                // The smaller one gets padding between chars (+1 on denominator - think fence posts and panels!)
                if (kheight > fheight) {
                    fpad = Math.round((float) (kheight - fheight) / (fend - fstart + 1));
                } else if (kheight < fheight) {
                    kpad = Math.round((float) (fheight - kheight) / (kend - kstart + 1));
                }

                // rendering
                int ystart = y;
                for (int j = kstart; j < kend; j++) {
                    // add in the padding - it might be zero
                    y += kpad;
                    // draw at the ascent line...
                    g2.drawChars(chars, j, 1, x, y + fonts.getTextFontAscent());
                    // ... then move on whole character size
                    y = y + fonts.getTextFontHeight();
                }
                y += kpad;
                int yend = y;

                y = ystart;
                g2.setFont(fonts.getFuriganaFont());
                for (int j = fstart; j < fend; j++) {
                    y += fpad;
                    g2.drawChars(chars, j, 1,
                            x + fonts.getTextCharWidth() + textToFuriganaSpacingAdjustment,  // adjustment to cwtch up to the kanji a bit
                            y + fonts.getFuriganaFontAscent());
                    y += fonts.getFuriganaFontHeight();
                }
                y = yend;
                i = fend;
                g2.setFont(fonts.getTextFont());
            } else {
                // draw at the ascent line...
                g2.drawChars(chars, i, 1, x, y + fonts.getTextFontAscent());
                // ... then move on whole character size
                y += fonts.getTextFontHeight();
            }
        }

        g2.dispose();
    }

    private boolean parseFurigana(char[] chars, int openBraceIndex) {
        kstart = openBraceIndex + 1;
        kend = chars.length;
        for (int j = kstart; j < chars.length; j++) {
            if (chars[j] == '}') {
                kend = j;
                break;
            }
        }
        // minimum valid syntax is `}{}`
        if (chars.length - kend < 3
                // next char must be `{`
                || chars[kend + 1] != '{') {
            return false;
        }
        fstart = kend + 2;
        fend = chars.length;
        for (int j = fstart; j < chars.length; j++) {
            if (chars[j] == '}') {
                fend = j;
                break;
            }
        }
        return fend != chars.length;
    }
}
