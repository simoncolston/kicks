package org.colston.kicks.document;

import jakarta.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum Tuning {
    HONCHOUSHI("本調子"),
    SANSAGE("三下げ"),
    NIAGE("二揚げ");

    private final String displayName;

    Tuning(String v) {
        this.displayName = v;
    }

    public String getDisplayName() {
        return displayName;
    }
}
