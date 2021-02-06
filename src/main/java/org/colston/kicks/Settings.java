package org.colston.kicks;

public interface Settings extends SettingsDataAccess {
    Character.Subset[] HIRAGANA = new Character.Subset[]{Character.UnicodeBlock.HIRAGANA};
    Character.Subset[] KATAKANA = new Character.Subset[]{Character.UnicodeBlock.KATAKANA};
    Character.Subset[] LATIN = new Character.Subset[]{Character.UnicodeBlock.BASIC_LATIN};
}