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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KicksABCImporterTest {

    private static final int MARGIN = 20;

    @ParameterizedTest
    @ValueSource(strings = {"asadoyayunta-kanji"})
    @ValueSource(strings = {"asadoyayunta"})
    @ValueSource(strings = {"sonda-2-chunjunnagari"})
    @ValueSource(strings = {"asadoya-and-nandaki"})
    @ValueSource(strings = {"ahabushi-thrice"})
    @ValueSource(strings = {"import-test"})
    @ValueSource(strings = {"_test-a"})
    void importFile(String filename) throws Exception {
        File inputFile = new File("testdata/" + filename + ".kicks");
        KicksDocument expected = DocumentStore.create().load(inputFile);

        File file = new File("testdata/" + filename + ".kicksabc");
        Optional<Importer> importer = ImporterFactory.getImporter(file);
        assertTrue(importer.isPresent());
        KicksDocument actual = importer.get().importFile(file);

        // TODO: remove this when kicksabc supports versions
        actual.getProperties().setVersion(expected.getProperties().getVersion());

        // do a diff of the kicks doc and the kicksabc doc
        UnitTestUtils.diff(expected, actual, List.of("/allLocatables"));

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}