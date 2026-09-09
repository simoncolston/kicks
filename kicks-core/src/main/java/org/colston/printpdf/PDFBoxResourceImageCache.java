package org.colston.printpdf;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

import java.io.IOException;

public class PDFBoxResourceImageCache extends PDFBoxResourceCache<PDFormXObject> {

    private final LayerUtility layerUtility; // puts the images into the document dictionary

    public PDFBoxResourceImageCache(LayerUtility layerUtility) {
        this.layerUtility = layerUtility;
    }

    protected PDFormXObject processDocument(PDDocument document) {
        try {
            return layerUtility.importPageAsForm(document, 0); // page 0
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
