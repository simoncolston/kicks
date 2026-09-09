package org.colston.printpdf;

import java.awt.*;

public interface PDFBoxDocumentRenderer {

    /**
     * Width of image.
     * @return width
     */
    float getWidth();

    /**
     * Height of image.
     * @return height
     */
    float getHeight();

    /**
     * Render the image to the graphics object.
     * @param g2 graphics
     */
    void render(Graphics2D g2);

    /**
     * Map of java font to pdfbox font for fonts required in this image.
     * @return font map
     */
    PDFBoxPrintFontMap getFontMap();
}
