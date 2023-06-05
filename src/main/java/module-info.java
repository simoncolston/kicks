module org.colston.kicks {
    requires java.desktop;
    requires java.logging;
    requires java.prefs;
    requires org.apache.pdfbox;
    requires jakarta.xml.bind;
    opens org.colston.kicks.document to jakarta.xml.bind;
}