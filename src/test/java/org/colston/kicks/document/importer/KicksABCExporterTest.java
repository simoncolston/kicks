package org.colston.kicks.document.importer;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.persistence.DocumentStore;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KicksABCExporterTest {

    @Test
    void exportDocument() throws Exception {
        File inputFile = new File("testdata/_test-a.kicks");
        KicksDocument inputDoc = DocumentStore.create().load(inputFile);

        File outputFile = new File("target/_test-a.kicksabc");
        KicksABCExporter exporter = new KicksABCExporter();
        exporter.exportDocument(inputDoc, outputFile);

        Optional<Importer> importer = ImporterFactory.getImporter(outputFile);
        assertTrue(importer.isPresent());
        KicksDocument outputDoc = importer.get().importFile(outputFile);

        // TODO: remove this when kicksabc supports versions
        outputDoc.getProperties().setVersion(inputDoc.getProperties().getVersion());

        // do a diff of the original doc and the round-trip via kicksabc doc
        DiffNode diff = ObjectDifferBuilder.buildDefault().compare(outputDoc, inputDoc);
        if (diff.hasChanges()) {
            diff.visit((node, visit) -> System.out.println(node.getPath() + " => " + node.getState()));
        }

        assertEquals(inputDoc, outputDoc);
    }
}