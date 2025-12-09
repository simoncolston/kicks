package org.colston.printpdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class PDFBoxGraphics2D extends Graphics2D implements Cloneable {
    /**
     * When we switch modes the previous mode is completed. e.g. the text will be written.
     */
    private enum Mode {
        NONE,   // neutral state
        TEXT,   // multiple text commands are executed in this mode
        STROKE  // multiple stroke commands are executed in this mode
    }

    /*
     * This is the object that this object was cloned from.  Allows a call to the parent to reset the state
     * of things like colour, stroke, etc. once this new context is finished.  Note that null indicates the root.
     */
    PDFBoxGraphics2D parent = null;

    /*
     * These fields are shared between instances of PDFBoxGraphics2D.  When objects are cloned these must stay at the
     * same value because we are writing to the same content stream.
     */
    private static class Shared {
        private PDPageContentStream cstream;
        public PDFBoxFontStore fontStore;
    }

    private final Shared shared = new Shared();

    private Mode mode = Mode.NONE;

    private Matrix transform = null;

    private Color colour = Color.BLACK;
    //only used with clearRect()
    private Color backgroundColour = Color.WHITE;
    private BasicStroke stroke = new BasicStroke(1.0f);
    private Font font = null;

    PDFBoxGraphics2D(PDPageContentStream cs, PDFBoxFontStore fontStore) {
        this.shared.cstream = cs;
        this.shared.fontStore = fontStore;
    }

    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    @Override
    protected PDFBoxGraphics2D clone() {
        try {
            PDFBoxGraphics2D g = (PDFBoxGraphics2D) super.clone();
            g.parent = this;
            g.transform = transform == null ? null : transform.clone();
            return g;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return this;
    }


    @Override
    public Graphics create() {
        checkMode(Mode.NONE);
        return clone();
    }

    @Override
    public void dispose() {
        checkMode(Mode.NONE);
        if (parent != null) {
            parent.restoreState();
        }
    }

    private void restoreState() {
        //TODO: something with the transform!?!

        setStroke(stroke);
        setColor(colour);
    }

    private void checkMode(Mode m) {
        if (mode == m) {
            return;
        }
        try {
            switch (mode) {
                case TEXT:
                    shared.cstream.endText();
                    break;
                case STROKE:
                    shared.cstream.stroke();
                    break;
                case NONE:
                    break;
            }
            mode = m;
            switch (mode) {
                case TEXT:
                    shared.cstream.beginText();
                    if (font == null) {
                        setFont(shared.fontStore.getDefaultFont());
                    }
                    break;
                case STROKE,
                     NONE:
                    break;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Shape s) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawString(String str, int x, int y) {
        drawString(str, (float) x, (float) y);
    }

    @Override
    public void drawString(String str, float x, float y) {
        try {
            checkMode(Mode.TEXT);
            Matrix m = Matrix.getTranslateInstance(x, -y);
            if (getFont().isTransformed()) {
                Matrix t = new Matrix(getFont().getTransform());
                m = Matrix.concatenate(m, t);
            }
            shared.cstream.setTextMatrix(m);
            shared.cstream.showText(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fill(Shape s) {
        checkMode(Mode.NONE);
        if (Color.WHITE.equals(colour)) {
            return;
        }
        try {
            float startx = 0f;
            float starty = 0f;
            float[] coords = new float[6];
            PathIterator pi = s.getPathIterator(null);
            while (!pi.isDone()) {
                int type = pi.currentSegment(coords);
                switch (type) {
                    case PathIterator.SEG_MOVETO -> {
                        startx = coords[0];
                        starty = coords[1];
                        shared.cstream.moveTo(startx, -starty);
                    }
                    case PathIterator.SEG_LINETO -> shared.cstream.lineTo(coords[0], -coords[1]);
                    case PathIterator.SEG_CLOSE -> shared.cstream.lineTo(startx, -starty);
                    case PathIterator.SEG_CUBICTO, PathIterator.SEG_QUADTO -> {
                        //TODO: not supported yet
                    }
                    default -> {
                    }
                }
                pi.next();
            }
            shared.cstream.fill();
            shared.cstream.closePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setComposite(Composite comp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setPaint(Paint paint) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStroke(Stroke s) {
        if (!(s instanceof BasicStroke)) {
            System.err.println("Only support BasicStroke implementation of Stroke");
            return;
        }
        checkMode(Mode.STROKE);
        this.stroke = (BasicStroke) s;
        try {
            shared.cstream.setLineWidth(stroke.getLineWidth());
            shared.cstream.setLineCapStyle(stroke.getEndCap());
            shared.cstream.setLineJoinStyle(stroke.getLineJoin());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object getRenderingHint(Key hintKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        // TODO Auto-generated method stub

    }

    @Override
    public RenderingHints getRenderingHints() {
        // TODO Auto-generated method stub
        return null;
    }

    private void applyTransform(Matrix m) {
        checkMode(Mode.NONE);
        try {
            shared.cstream.transform(m);
            transform = transform == null ? m : Matrix.concatenate(transform, m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void translate(int x, int y) {
        translate(x, (double) y);
    }

    @Override
    public void translate(double tx, double ty) {
        Matrix m = Matrix.getTranslateInstance((float) tx, (float) -ty);
        applyTransform(m);
    }

    @Override
    public void rotate(double theta) {
        Matrix m = Matrix.getRotateInstance(theta, 0, 0);
        applyTransform(m);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        Matrix m = Matrix.getTranslateInstance((float) x, (float) -y);
        applyTransform(m);
        m = Matrix.getRotateInstance(theta, 0, 0);
        applyTransform(m);
        m = Matrix.getTranslateInstance((float) -x, (float) y);
        applyTransform(m);
    }

    @Override
    public void scale(double sx, double sy) {
        Matrix m = Matrix.getScaleInstance((float) sx, (float) sy);
        applyTransform(m);
    }

    @Override
    public void shear(double shx, double shy) {
        Matrix m = new Matrix(1, (float) shx, (float) shy, 1, 0, 0);
        applyTransform(m);
    }

    @Override
    public void transform(AffineTransform Tx) {
        Matrix m = new Matrix(Tx);
        applyTransform(m);
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        System.err.println("setTransform(AffineTransform Tx) is not supported.");
    }

    @Override
    public AffineTransform getTransform() {
        return transform == null ? new AffineTransform() : transform.createAffineTransform();
    }

    @Override
    public Paint getPaint() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Composite getComposite() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBackground(Color colour) {
        this.backgroundColour = colour;
    }

    @Override
    public Color getBackground() {
        return backgroundColour;
    }

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public void clip(Shape s) {
        // TODO Auto-generated method stub

    }

    @Override
    public FontRenderContext getFontRenderContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Color getColor() {
        return colour;
    }

    @Override
    public void setColor(Color c) {
        //Need to commit everything in current colour before setting the new one
        checkMode(Mode.NONE);
        try {
            shared.cstream.setStrokingColor(c);
            shared.cstream.setNonStrokingColor(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.colour = c;
    }

    @Override
    public void setPaintMode() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setXORMode(Color c1) {
        // TODO Auto-generated method stub

    }

    @Override
    public Font getFont() {
        return font == null ? shared.fontStore.getDefaultFont() : font;
    }

    @Override
    public void setFont(Font font) {
        PDFont pdfont = shared.fontStore.get(font);
        if (pdfont == null) {
            System.err.println("There is no font mapping for font: " + font);
            return;
        }
        try {
            shared.cstream.setFont(pdfont, font.getSize());
            this.font = font;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        PDFont pdfont = shared.fontStore.get(f);
        if (pdfont == null) {
            //TODO: runtime exception?
            return null;
        }
        return new PDFBoxFontMetrics(f, pdfont);
    }

    @Override
    public Rectangle getClipBounds() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public Shape getClip() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setClip(Shape clip) {
        // TODO Auto-generated method stub

    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        checkMode(Mode.STROKE);
        try {
            shared.cstream.moveTo(x1, -y1);
            shared.cstream.lineTo(x2, -y2);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        checkMode(Mode.NONE);
        if (Color.WHITE.equals(colour)) {
            return;
        }
        try {
            shared.cstream.addRect(x, -y, width, -height);
            shared.cstream.fill();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        // TODO NOTE: use background colour, then reset to colour!

    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
                             ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
                             Color bgcolor, ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }
}
