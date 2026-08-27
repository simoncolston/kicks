package org.colston.kicks.document.persistence;

import org.colston.kicks.document.KicksDocument;

import java.io.*;
import java.nio.file.Files;

public class KicksABCDocumentStore implements DocumentStore {

    @Override
    public KicksDocument load(InputStream is) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            KicksABCImporter importer = new KicksABCImporter();
            return importer.load(br);
        }
    }

    @Override
    public KicksDocument load(File file) throws Exception {
        try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
            KicksABCImporter importer = new KicksABCImporter();
            return importer.load(br);
        }
    }

    @Override
    public void save(KicksDocument doc, File file) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            KicksABCExporter exporter = new KicksABCExporter();
            exporter.save(doc, writer);
        }
    }

    @Override
    public void save(KicksDocument doc, OutputStream os) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
            KicksABCExporter exporter = new KicksABCExporter();
            exporter.save(doc, writer);
        }
    }
}
