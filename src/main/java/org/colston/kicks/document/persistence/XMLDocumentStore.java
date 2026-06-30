package org.colston.kicks.document.persistence;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.Layout;
import org.colston.lib.xml.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class XMLDocumentStore implements DocumentStore {

    private final static Logger log = Logger.getLogger(XMLDocumentStore.class.getName());

    private final Map<Integer, Schema> schemas = new HashMap<>();

    void initialise() throws Exception {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL url = XMLDocumentStore.class.getResource("kunkunshi-v1.xsd");
        schemas.put(1, sf.newSchema(url));
        url = XMLDocumentStore.class.getResource("kunkunshi-v2.xsd");
        schemas.put(2, sf.newSchema(url));
    }

    /**
     * Load document from file.
     * Forward version conversion is performed.
     * @param file file containing kicks document xml
     * @return the loaded document
     * @throws Exception error reading file or parsing file
     */
    @Override
    public KicksDocument load(File file) throws Exception {
        int version = extractVersion(file);
        if (version == KicksDocument.CURRENT_VERSION) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
                return load(is);
            }
        }
        // need to version up
        // start by parsing to an XML DOM
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringElementContentWhitespace(true); // doesn't work!
        dbf.setSchema(schemas.get(version));
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        while (version < KicksDocument.CURRENT_VERSION) {

            // version up
            version1to2(doc);
            version++;
        }
        // now, unmarshall from the DOM (that should now be at the current version)
        JAXBContext context = JAXBContext.newInstance(KicksDocument.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schemas.get(KicksDocument.CURRENT_VERSION));
        return (KicksDocument) unmarshaller.unmarshal(doc);
    }

    void version1to2(Document doc) {
        Element kunkunshi = doc.getDocumentElement();
        log.info("Converting version 1 to 2...");

        // remove transcription property and move value to song zero
        Element properties = XML.getElement(kunkunshi, "properties");
        Element transcription  = XML.getElement(properties, "transcription");
        if (transcription != null) {
            String transcriptionValue = transcription.getTextContent();
            log.info("Moving transcription '" + transcriptionValue + "' to song");
            properties.removeChild(transcription);
            Element songs = XML.getElement(kunkunshi, "songs");
            Element song = XML.item(songs, 0);
            if (song != null) {
                song.appendChild(transcription);
            }
        }

        // move all notes, etc. forward 12 cells
        int cells = Layout.LANDSCAPE_11COL_12CELL.getCellsPerColumn();
        Element notes = XML.getElement(kunkunshi, "notes");
        Element repeats = XML.getElement(kunkunshi, "repeats");
        Element lyrics = XML.getElement(kunkunshi, "lyrics");
        for (Element note : XML.children(notes) ) {
            note.setAttribute("index", String.valueOf(Integer.parseInt(note.getAttribute("index")) + cells));
        }
        for (Element repeat : XML.children(repeats) ) {
            repeat.setAttribute("index", String.valueOf(Integer.parseInt(repeat.getAttribute("index")) + cells));
        }
        for (Element lyric : XML.children(lyrics) ) {
            lyric.setAttribute("index", String.valueOf(Integer.parseInt(lyric.getAttribute("index")) + cells));
        }

        // set the version number
        kunkunshi.setAttribute("version", "2");
        log.info("Version 1 to 2 complete.");
    }

    private int extractVersion(File file) throws Exception {

        XMLInputFactory xif = XMLInputFactory.newFactory();
        StreamSource xml = new StreamSource(file);
        XMLStreamReader xsr = xif.createXMLStreamReader(xml);
        try {
            // Check the version attribute
            xsr.nextTag(); // Advance to root element
            int version = Integer.parseInt(xsr.getAttributeValue("", "version"));
            if(!schemas.containsKey(version)) {
                throw new Exception("Unknown version: " + version);
            }
            return version;
        } finally {
            xsr.close();
        }
    }

    /**
     * Load a document from the given <code>InputStream</code>.
     * Note that version conversion is not performed.
     * @param is input stream
     * @return the loaded document
     * @throws Exception error reading or parsing from input stream
     */
    @Override
    public KicksDocument load(InputStream is) throws Exception {
        JAXBContext context = JAXBContext.newInstance(KicksDocument.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schemas.get(KicksDocument.CURRENT_VERSION));

        return (KicksDocument) unmarshaller.unmarshal(is);
    }

    @Override
    public void save(KicksDocument doc, OutputStream os) throws Exception {
        JAXBContext context = JAXBContext.newInstance(KicksDocument.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setSchema(schemas.get(KicksDocument.CURRENT_VERSION));

        marshaller.marshal(doc, os);
    }

    @Override
    public KicksDocument clone(KicksDocument doc) {
        try {
            JAXBContext context = JAXBContext.newInstance(KicksDocument.class);

            StringWriter sw = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setSchema(schemas.get(KicksDocument.CURRENT_VERSION));
            marshaller.marshal(doc, sw);

            String str = sw.toString();
            StringReader sr = new StringReader(str);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (KicksDocument) unmarshaller.unmarshal(sr);
        } catch (JAXBException e) {
            //TODO logger
            e.printStackTrace();
        }
        return null;
    }
}
