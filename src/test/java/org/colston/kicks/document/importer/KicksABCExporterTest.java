package org.colston.kicks.document.importer;

import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.persistence.DocumentStore;
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
//    private final static String INPUT_DIR = "/home/simon/Drive/sanshin/mykunkunshi/kicks/eisa/";
//    private final static String OUTPUT_DIR = "/home/simon/Drive/sanshin/mykunkunshi/kicksabc/eisa/";

    @ParameterizedTest
//    @ValueSource(strings = {"sonda-1-nandakibushi"})
//    @ValueSource(strings = {"sonda-2-chunjunnagari"})
//    @ValueSource(strings = {"sonda-3-kudaka"})
//    @ValueSource(strings = {"sonda-4-sunsami"})
//    @ValueSource(strings = {"sonda-5-tutankani"})
//    @ValueSource(strings = {"sonda-6-umiyakara"})
//    @ValueSource(strings = {"sonda-7-tenyobushi"})
//    @ValueSource(strings = {"sonda-8-ichubigwabushi"})
//    @ValueSource(strings = {"sonda-9-katamibushi"})
    @ValueSource(strings = {"_test-a"})
    @ValueSource(strings = {"asadoyayunta"})
    @ValueSource(strings = {"sonda-2-chunjunnagari"})
    @ValueSource(strings = {"asadoya-and-nandaki"})
    @ValueSource(strings = {"ahabushi-thrice"})
    void exportDocument(String filename) throws Exception {
        File inputFile = new File(INPUT_DIR + filename + ".kicks");
        KicksDocument inputDoc = DocumentStore.create().load(inputFile);

        File outputFile = new File(OUTPUT_DIR + filename + ".kicksabc");
        KicksABCExporter exporter = new KicksABCExporter();
        exporter.exportDocument(inputDoc, outputFile);

        Optional<Importer> importer = ImporterFactory.getImporter(outputFile);
        assertTrue(importer.isPresent());
        KicksDocument outputDoc = importer.get().importFile(outputFile);

        // TODO: remove this when kicksabc supports versions
        outputDoc.getProperties().setVersion(inputDoc.getProperties().getVersion());

        // do a diff of the original doc and the round-trip via kicksabc doc
        UnitTestUtils.diff(inputDoc, outputDoc, List.of("/allLocatables"));

        assertEquals(inputDoc, outputDoc);
    }
}