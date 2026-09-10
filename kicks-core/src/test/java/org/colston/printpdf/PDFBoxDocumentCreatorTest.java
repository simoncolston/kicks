package org.colston.printpdf;

import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.persistence.DocumentStore;
import org.colston.kicks.document.persistence.DocumentStoreFactory;
import org.colston.kicks.render.KicksDocumentRenderer;
import org.colston.kicks.render.PageRenderer;
import org.colston.kicks.render.RendererResources;
import org.junit.jupiter.api.Test;

import javax.print.attribute.standard.OrientationRequested;
import java.awt.*;
import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PDFBoxDocumentCreatorTest {

    @Test
    void minimumCanvas() throws Exception {
        File file = new File("testdata/import-test.kicks");
        Optional<DocumentStore> documentStore = DocumentStoreFactory.create(file);
        assertTrue(documentStore.isPresent());
        KicksDocument kicksDocument = documentStore.get().load(file);

        PageRenderer pageRenderer = PageRenderer.create(kicksDocument);
        KicksDocumentRenderer renderer = new KicksDocumentRenderer(pageRenderer);

        PDFBoxDocumentCreator creator = PDFBoxDocumentCreator.getInstance().asImage(true);
        creator.save(renderer, new File("target/test-pdfimage-minimumCanvas.pdf"));
    }

    @Test
    void multiPageA4Landscape() throws Exception {
        File file = new File("testdata/eisa-sonda.kicksabc");
        Optional<DocumentStore> documentStore = DocumentStoreFactory.create(file);
        assertTrue(documentStore.isPresent());
        KicksDocument kicksDocument = documentStore.get().load(file);

        PageRenderer pageRenderer = PageRenderer.create(kicksDocument);
        KicksDocumentRenderer renderer = new KicksDocumentRenderer(pageRenderer);

        PDFBoxDocumentCreator creator = PDFBoxDocumentCreator.getInstance()
                .orientation(OrientationRequested.LANDSCAPE);
        creator.save(renderer, new File("target/test-pdfimage-multipageA4Landscape.pdf"));
    }
}