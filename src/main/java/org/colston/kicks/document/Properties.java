package org.colston.kicks.document;

import jakarta.xml.bind.annotation.XmlType;

import java.util.Objects;

@XmlType(propOrder =
        {
                "name",
                "description",
                "version",
                "layout",
                "transcription"
        })
public class Properties {
    private String name;
    private String description;
    private String version;
    private Layout layout = Layout.LANDSCAPE_11COL_12CELL;
    private String transcription;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout l) {
        this.layout = l;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Properties that = (Properties) o;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(version, that.version)
                && layout == that.layout
                && Objects.equals(transcription, that.transcription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, version, layout, transcription);
    }
}
