package org.colston.printpdf;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFBoxResourceDocumentCache extends PDFBoxResourceCache<PDDocument> {

    protected PDDocument processDocument(PDDocument document) {
        // NOOP, just caching the document
        return document;
    }
}
