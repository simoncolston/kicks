package org.colston.kicks.document.persistence;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.colston.kicks.document.KicksDocument;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

public class XMLDocumentStore implements DocumentStore {
    private Schema schema;

    void initialise() throws Exception {
        URL url = XMLDocumentStore.class.getResource("kunkunshi.xsd");
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schema = sf.newSchema(url);
    }

    @Override
    public KicksDocument load(InputStream is) throws Exception {
        JAXBContext context = JAXBContext.newInstance(KicksDocument.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schema);

        return (KicksDocument) unmarshaller.unmarshal(is);
    }

    @Override
    public void save(KicksDocument doc, OutputStream os) throws Exception {
        JAXBContext context = JAXBContext.newInstance(KicksDocument.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setSchema(schema);

        marshaller.marshal(doc, os);
    }

    @Override
    public KicksDocument clone(KicksDocument doc) {
        try {
            JAXBContext context = JAXBContext.newInstance(KicksDocument.class);

            StringWriter sw = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setSchema(schema);
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
