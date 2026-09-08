package org.colston.printpdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

public class PDFBoxResourceImage extends Image {

    private final Class<?> referenceClass;
    private final String resourceName;
    private AffineTransform transform;
    // lazy load the image if needed
    private PDDocument pdfImage;

    public PDFBoxResourceImage(Class<?> referenceClass, String resourceName) {
        this.referenceClass = referenceClass;
        this.resourceName = resourceName;
    }

    public Class<?> getReferenceClass() {
        return referenceClass;
    }

    public String getResourceName() {
        return resourceName;
    }

    public AffineTransform getTransform() {
        return transform;
    }

    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }

    @Override
    public int getWidth(ImageObserver imageObserver) {
        lazyLoadResourceImage(referenceClass, resourceName);
        return pdfImage != null ? (int) pdfImage.getPage(0).getBBox().getWidth() : 0;
    }

    @Override
    public int getHeight(ImageObserver imageObserver) {
        lazyLoadResourceImage(referenceClass, resourceName);
        return pdfImage != null ? (int) pdfImage.getPage(0).getBBox().getHeight() : 0;
    }

    @Override
    public ImageProducer getSource() {
        return null;
    }

    @Override
    public Graphics getGraphics() {
        return null;
    }

    @Override
    public Object getProperty(String s, ImageObserver imageObserver) {
        return null;
    }

    private void lazyLoadResourceImage(Class<?> clazz, String resourceName) {
        if (pdfImage != null) {
            return;
        }
        try (BufferedInputStream bis = new BufferedInputStream(
                Objects.requireNonNull(clazz.getResourceAsStream(resourceName)))) {
            pdfImage = Loader.loadPDF(new RandomAccessReadBuffer(bis));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
