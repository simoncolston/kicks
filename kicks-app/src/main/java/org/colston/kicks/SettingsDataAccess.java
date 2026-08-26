package org.colston.kicks;

import java.util.EventListener;

public interface SettingsDataAccess {
    Character.Subset[] getCharacterSubset();

    void setCharacterSubset(Character.Subset[] characterSubset);

    boolean isOpenPdfAfterExport();

    void setOpenPdfAfterExport(boolean openPdfAfterExport);

    boolean isRomaji();

    void setRomaji(boolean romaji);


    void addListener(Listener listener);
    void removeListener(Listener listener);

    interface Listener extends EventListener {
        void settingChanged();
    }
}