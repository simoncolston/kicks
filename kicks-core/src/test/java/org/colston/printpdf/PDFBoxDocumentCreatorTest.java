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

class PDFBoxDocumentCreatorTest {

    @Test
    void create() throws Exception {
        File file = new File("testdata/eisa-sonda.kicksabc");
//        File file = new File("testdata/import-test.kicks");
        Optional<DocumentStore> documentStore = DocumentStoreFactory.create(file);
        assertTrue(documentStore.isPresent());
        KicksDocument kicksDocument = documentStore.get().load(file);

        PageRenderer pageRenderer = PageRenderer.create(kicksDocument).useMinimumCanvas(true);
        PDFBoxDocumentRenderer renderer = new PDFBoxDocumentRenderer() {
            @Override
            public float getWidth() {
                return pageRenderer.getCanvasWidth(0);
            }

            @Override
            public float getHeight() {
                return pageRenderer.getCanvasHeight();
            }

            @Override
            public void render(Graphics2D g2) {
                pageRenderer.doPaint(g2, 0);
            }

            @Override
            public PDFBoxPrintFontMap getFontMap() {
                return RendererResources.createFontMap();
            }
        };


        PDFBoxDocumentCreator creator = new PDFBoxDocumentCreator();
        creator.save(renderer, new File("target/test-pdfimage.pdf"));
    }
}