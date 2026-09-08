package org.colston.printpdf;

import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.persistence.DocumentStore;
import org.colston.kicks.document.persistence.DocumentStoreFactory;
import org.colston.kicks.render.PageRenderer;
import org.colston.kicks.render.RendererResources;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PDFBoxImageDocumentCreatorTest {

    @Test
    void create() throws Exception {
        PDFBoxImageDocumentCreator creator = new PDFBoxImageDocumentCreator();
//        File file = new File("testdata/eisa-sonda.kicksabc");
        File file = new File("testdata/import-test.kicks");
        Optional<DocumentStore> documentStore = DocumentStoreFactory.create(file);
        assertTrue(documentStore.isPresent());
        KicksDocument kicksDocument = documentStore.get().load(file);
        PageRenderer pageRenderer = PageRenderer.create(kicksDocument, 0).useMinimumCanvas();
        PDFBoxImageDocumentRenderer renderer = new PDFBoxImageDocumentRenderer() {
            @Override
            public float getWidth() {
                return pageRenderer.getCanvasWidth();
            }

            @Override
            public float getHeight() {
                return pageRenderer.getCanvasHeight();
            }

            @Override
            public void render(Graphics2D g2) {
                pageRenderer.doPaint(g2);
            }

            @Override
            public PDFBoxPrintFontMap getFontMap() {
                return RendererResources.createFontMap();
            }
        };
        creator.save(renderer, new File("target/test-pdfimage.pdf"));
    }
}