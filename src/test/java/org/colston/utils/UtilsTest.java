package org.colston.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void toRomaji() {
        String romaji = Utils.toRomaji("サ");
        assertEquals("sa", romaji);
    }

    @Test
    void toKatakana() {
        String katakana = Utils.toKatakana("sa");
        assertEquals("サ", katakana);
        katakana = Utils.toKatakana("gwa");
        assertEquals("グヮ", katakana);
        katakana = Utils.toKatakana("ti");
        assertEquals("ティ", katakana);
        katakana = Utils.toKatakana("zz");
        assertEquals("zz", katakana);
    }
}