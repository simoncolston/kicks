package org.colston.kicks;

public interface SettingsDataAccess {
    Character.Subset[] getCharacterSubset();

    void setCharacterSubset(Character.Subset[] characterSubset);
}