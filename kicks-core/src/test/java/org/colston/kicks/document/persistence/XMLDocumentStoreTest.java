package org.colston.kicks.document.persistence;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

class XMLDocumentStoreTest {

    @Test
    void version1to2() throws ParserConfigurationException, IOException, SAXException, JAXBException, TransformerException {
        XMLDocumentStore store = new XMLDocumentStore();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File("testdata/_test.kicks"));

        store.version1to2(doc);

//        Transformer transformer = TransformerFactory.newInstance().newTransformer();
//        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//        transformer.transform(new DOMSource(doc), new StreamResult(System.out));
    }
}