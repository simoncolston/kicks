package org.colston.printpdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PDFBoxResourceImageCache {

    private final LayerUtility layerUtility; // puts the images into the document dictionary
    private final Map<Class<?>, Map<String, PDFormXObject>> cache = new HashMap<>();

    public PDFBoxResourceImageCache(LayerUtility layerUtility) {
        this.layerUtility = layerUtility;
    }

    public PDFormXObject get(Class<?> clazz, String resourceName) {
        Map<String, PDFormXObject> map = cache.computeIfAbsent(clazz, k -> new HashMap<>());
        return map.computeIfAbsent(resourceName, key -> loadResourceImage(clazz, key));
    }

    private PDFormXObject loadResourceImage(Class<?> clazz, String resourceName) {
        try (BufferedInputStream bis = new BufferedInputStream(
                Objects.requireNonNull(clazz.getResourceAsStream(resourceName)))) {
            PDDocument pdfImage = Loader.loadPDF(new RandomAccessReadBuffer(bis));
            return layerUtility.importPageAsForm(pdfImage, 0); // page 0
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
