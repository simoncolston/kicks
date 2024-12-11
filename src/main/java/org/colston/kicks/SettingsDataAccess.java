package org.colston.kicks;

public interface SettingsDataAccess {
    Character.Subset[] getCharacterSubset();

    void setCharacterSubset(Character.Subset[] characterSubset);

    boolean isOpenPdfAfterExport();

    void setOpenPdfAfterExport(boolean openPdfAfterExport);
}