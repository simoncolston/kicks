package org.colston.printpdf;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public class PDFBoxResourceImage extends Image {

    private final Class<?> referenceClass;
    private final String resourceName;
    private AffineTransform transform;

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
        return 0;
    }

    @Override
    public int getHeight(ImageObserver imageObserver) {
        return 0;
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
}
