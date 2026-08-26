package org.colston.kicks;

import javax.swing.event.EventListenerList;

public class SettingsData implements SettingsDataAccess {

    private Character.Subset[] characterSubset = Settings.KATAKANA;
    private boolean openPdfAfterExport = true;
    private boolean romaji = false;

    private final EventListenerList listeners = new EventListenerList();


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

    @Override
    public boolean isRomaji() {
        return romaji;
    }

    @Override
    public void setRomaji(boolean romaji) {
        if (this.romaji != romaji) {
            this.romaji = romaji;
            fireSettingsUpdated();
        }
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(Listener.class, listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(Listener.class, listener);
    }

    protected void fireSettingsUpdated() {
        for (Listener l : listeners.getListeners(Listener.class)) {
            l.settingChanged();
        }
    }
}
