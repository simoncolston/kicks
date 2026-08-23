package org.colston.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KanaConverterTest {

    @Test
    void toRomaji() {
        String romaji = KanaConverter.toRomaji("サ");
        assertEquals("sa", romaji);
    }

    @Test
    void toKatakana() {
        String katakana = KanaConverter.toKatakana("sa");
        assertEquals("サ", katakana);
        katakana = KanaConverter.toKatakana("gwa");
        assertEquals("グヮ", katakana);
        katakana = KanaConverter.toKatakana("ti");
        assertEquals("ティ", katakana);
        katakana = KanaConverter.toKatakana("zz");
        assertEquals("zz", katakana);
    }
}
