package org.colston.printpdf;

import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.io.IOException;

public class PDFBoxFontMetrics extends FontMetrics {

    private final PDFont pdfont;

    protected PDFBoxFontMetrics(Font font, PDFont pdfont) {
        super(font);
        this.pdfont = pdfont;
    }

    @Override
    public int getHeight() {
        return (int) (pdfont.getFontDescriptor().getFontBoundingBox().getHeight() * getFont().getSize());
    }


    @Override
    public int getAscent() {
        return getHeight();
    }


    @Override
    public int getMaxAdvance() {
        return stringWidth("中");
    }

    @Override
    public int charWidth(char ch) {
        char[] data = {ch};
        return stringWidth(new String(data, 0, 1));
    }

    @Override
    public int stringWidth(String str) {
        try {
            return (int) pdfont.getStringWidth(str) * getFont().getSize() / 1000;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
