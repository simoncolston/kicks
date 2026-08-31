package org.colston.kicks.document.persistence;

import org.colston.kicks.document.KicksDocument;
import org.colston.unittestutils.UnitTestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class KicksABCImporterTest {

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
        KicksDocument expected = DocumentStoreFactory.createDefault().load(inputFile);

        File file = new File("testdata/" + filename + ".kicksabc");
        Optional<DocumentStore> abcStore = DocumentStoreFactory.create(file);
        assertTrue(abcStore.isPresent());

        KicksDocument actual = abcStore.get().load(file);

        // TODO: remove this when kicksabc supports versions
        actual.getProperties().setVersion(expected.getProperties().getVersion());

        // do a diff of the kicks doc and the kicksabc doc
        UnitTestUtils.diff(expected, actual, List.of("/allLocatables"));

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}