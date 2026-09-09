package org.colston.printpdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class PDFBoxResourceCache<T> {

    private final Map<Class<?>, Map<String, T>> cache = new HashMap<>();

    public T get(Class<?> clazz, String resourceName) {
        Map<String, T> map = cache.computeIfAbsent(clazz, k -> new HashMap<>());
        PDDocument document = loadResourceDocument(clazz, resourceName);
        return map.computeIfAbsent(resourceName, key -> processDocument(document));
    }

    protected abstract T processDocument(PDDocument document);

    private PDDocument loadResourceDocument(Class<?> clazz, String resourceName) {
        try (BufferedInputStream bis = new BufferedInputStream(
                Objects.requireNonNull(clazz.getResourceAsStream(resourceName)))) {
            return Loader.loadPDF(new RandomAccessReadBuffer(bis));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
