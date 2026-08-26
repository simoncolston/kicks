package org.colston.kicks.document;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class SimpleLocatableTest {

    @Test
    void clear() {
        SimpleLocatable locatable = new SimpleLocatable(10, 6);
        locatable.clear();
        assertEquals(-1, locatable.getIndex());
        assertEquals(-1, locatable.getOffset());
    }

    @ParameterizedTest
    @CsvSource({"11, 1, 10, 6, 0, 7",
            "9, 11, 10, 6, 0, -7"})
    void move(int expectedIndex, int expectedOffset, int locatableIndex, int locatableOffset, int moveIndex, int moveOffset) {
        SimpleLocatable expected = new SimpleLocatable(expectedIndex, expectedOffset);
        SimpleLocatable locatable = new SimpleLocatable(locatableIndex, locatableOffset);
        locatable.move(moveIndex, moveOffset);
        assertEquals(expected, locatable);
    }

    @Test
    void isLessThan() {
        SimpleLocatable locatable1 = new SimpleLocatable(5, 3);
        SimpleLocatable locatable2 = new SimpleLocatable(10, 6);
        assertTrue(locatable1.isLessThan(locatable2));
    }

    @Test
    void isGreaterThan() {
        SimpleLocatable locatable1 = new SimpleLocatable(5, 3);
        SimpleLocatable locatable2 = new SimpleLocatable(10, 6);
        System.out.println(locatable1.getTicks() + ", " + locatable2.getTicks());
        assertTrue(locatable2.isGreaterThan(locatable1));
    }

    @Test
    void isEqualTo() {
        SimpleLocatable locatable1 = new SimpleLocatable(10, 6);
        SimpleLocatable locatable2 = new SimpleLocatable(10, 6);
        assertTrue(locatable1.isEqualTo(locatable2));
    }
}