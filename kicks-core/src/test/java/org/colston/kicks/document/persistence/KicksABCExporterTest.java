package org.colston.kicks.document.persistence;

import org.colston.kicks.document.KicksDocument;
import org.colston.unittestutils.UnitTestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KicksABCExporterTest {

    private final static String INPUT_DIR = "testdata/";
    private final static String OUTPUT_DIR = "target/";

    @ParameterizedTest
    @ValueSource(strings = {"export-_test-a"})
    @ValueSource(strings = {"asadoyayunta"})
    @ValueSource(strings = {"sonda-2-chunjunnagari"})
    @ValueSource(strings = {"asadoya-and-nandaki"})
    @ValueSource(strings = {"ahabushi-thrice"})
    void exportDocument(String filename) throws Exception {
        File inputFile = new File(INPUT_DIR + filename + ".kicks");
        KicksDocument inputDoc = DocumentStoreFactory.createDefault().load(inputFile);

        File outputFile = new File(OUTPUT_DIR + filename + ".kicksabc");
        Optional<DocumentStore> abcStore = DocumentStoreFactory.create(outputFile);
        assertTrue(abcStore.isPresent());
        abcStore.get().save(inputDoc, outputFile);

        KicksDocument outputDoc = abcStore.get().load(outputFile);

        // TODO: remove this when kicksabc supports versions
        outputDoc.getProperties().setVersion(inputDoc.getProperties().getVersion());

        // do a diff of the original doc and the round-trip via kicksabc doc
        UnitTestUtils.diff(inputDoc, outputDoc, List.of("/allLocatables"));

        assertEquals(inputDoc, outputDoc);
    }
}