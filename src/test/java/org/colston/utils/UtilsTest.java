package org.colston.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void toRomaji() {
        String romaji = Utils.toRomaji("サ");
        assertEquals("sa", romaji);
    }
}