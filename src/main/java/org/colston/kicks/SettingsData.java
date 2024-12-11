package org.colston.kicks;

public class SettingsData implements SettingsDataAccess {
    private Character.Subset[] characterSubset = Settings.KATAKANA;
    private boolean openPdfAfterExport = true;

    @Override
    public Character.Subset[] getCharacterSubset() {
        return characterSubset;
    }

    @Override
    public void setCharacterSubset(Character.Subset[] characterSubset) {
        this.characterSubset = characterSubset;
    }

    @Override
    public boolean isOpenPdfAfterExport() {
        return openPdfAfterExport;
    }

    @Override
    public void setOpenPdfAfterExport(boolean openPdfAfterExport) {
        this.openPdfAfterExport = openPdfAfterExport;
    }
}
