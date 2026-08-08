package org.colston.kicks.document.importer;

import org.colston.kicks.KicksApp;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.persistence.DocumentStore;
import org.colston.kicks.gui.canvas.PageRenderer;
import org.colston.printpdf.PDFBoxPrintFontMap;
import org.colston.printpdf.PDFBoxPrintService;
import org.colston.unittestutils.UnitTestUtils;
import org.colston.utils.Utils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import java.awt.*;
import java.awt.print.Printable;
import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KicksABCImporterTest {

    private static final int MARGIN = 20;

    @ParameterizedTest
    @ValueSource(strings = {"asadoyayunta"})
    @ValueSource(strings = {"sonda-2-chunjunnagari"})
    @ValueSource(strings = {"asadoya-and-nandaki"})
    @ValueSource(strings = {"ahabushi-thrice"})
    @ValueSource(strings = {"import-test"})
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

//        exportToPDF(filename, outputDoc);
    }

    private void exportToPDF(String filename, KicksDocument kicksDocument) throws Exception {
        Printable printable = new PageRenderer(kicksDocument, null);
        File destination = new File("target/" + filename + ".pdf");
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(MediaSizeName.ISO_A4);
        aset.add(OrientationRequested.LANDSCAPE);

        aset.add(new Destination(destination.toURI()));

        String jobName = destination.getName();
        jobName = jobName.substring(0, jobName.lastIndexOf(Utils.PDF_FILE_EXT));
        aset.add(new JobName(jobName, null));

        //TODO:  The whole 'lookup print service' thing - looks fun!
        PrintService pservice = new PDFBoxPrintService();

        DocPrintJob printJob = pservice.createPrintJob();
        Doc doc = new SimpleDoc(printable, flavor, /* daset */ null);

        MediaSize mediaSize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A4);
        float width = mediaSize.getX(Size2DSyntax.MM) - (MARGIN * 2);
        float height = mediaSize.getY(Size2DSyntax.MM) - (MARGIN * 2);
        MediaPrintableArea mpa = new MediaPrintableArea(MARGIN, MARGIN, width, height, MediaPrintableArea.MM);
        aset.add(mpa);

        PDFBoxPrintFontMap fontMap = new PDFBoxPrintFontMap();
        Font font = new Font(KicksApp.FONT_NAME, Font.PLAIN, 1);
        fontMap.add(font, KicksApp.class, KicksApp.FONT_RESOURCE_NAME);
        font = new Font(KicksApp.V_FONT_NAME, Font.PLAIN, 1);
        fontMap.add(font, KicksApp.class, KicksApp.V_FONT_RESOURCE_NAME);
        aset.add(fontMap);

        printJob.print(doc, aset);
    }
}