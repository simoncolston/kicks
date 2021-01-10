package org.colston.kicks;

public class SettingsData implements SettingsDataAccess {
    private Character.Subset[] characterSubset = Settings.KATAKANA;

    @Override
    public Character.Subset[] getCharacterSubset() {
        return characterSubset;
    }

    @Override
    public void setCharacterSubset(Character.Subset[] characterSubset) {
        this.characterSubset = characterSubset;
    }
}
