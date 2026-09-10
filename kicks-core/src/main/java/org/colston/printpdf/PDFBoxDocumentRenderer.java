package org.colston.printpdf;

import java.awt.*;

public interface PDFBoxDocumentRenderer {

    /**
     * Requests an image (minimum sized document) rather than a normal pdf document on A4 pages, for example.
     * @param asImage true if an image is required
     */
    void asImage(boolean asImage);

    /**
     * Width of image.  Used with {@link #asImage(boolean)}.
     * @return width
     */
    float getWidth(int pageIndex);

    /**
     * Height of image. Used with {@link #asImage(boolean)}
     * @return height
     */
    float getHeight(int pageIndex);

    /**
     * Render the document/image to the graphics object.
     * @param g2 graphics
     */
    void render(Graphics2D g2,  int pageIndex);

    /**
     * Map of java font to pdfbox font for fonts required in this document/image.
     * @return font map
     */
    PDFBoxPrintFontMap getFontMap();

    /**
     * Number of pages in the document.
     * @return number of pages
     */
    int getNumberOfPages();
}
