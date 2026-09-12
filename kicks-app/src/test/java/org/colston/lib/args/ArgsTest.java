package org.colston.lib.args;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArgsTest {

    @Test
    void parse() {

        String a = "--cli --to-pdf --romaji-lyrics --filename-suffix=romaji --output-dir=tmp kicks/_test.kicks";
        String[] args = a.split(" ");
        Args argss = Args.builder()
                .withVargs(true)
                .flag("--cli", "Run on command line without GUI")
                .flag("--to-pdf", "Convert document to pdf")
                .flag("--romaji-lyrics", "Convert the lyrics to romaji")
                .parameter("--filename-suffix", "Suffix to add to the output file name")
                .parameter("--output-dir", "Output directory")
                .parse(args);
        assertTrue(argss.is("--cli"));
        assertTrue(argss.is("--to-pdf"));
        assertTrue(argss.is("--romaji-lyrics"));
        assertEquals("romaji", argss.get("--filename-suffix"));
        assertEquals("tmp", argss.get("--output-dir"));
        assertEquals(1, argss.getVargs().size());
        assertEquals("kicks/_test.kicks", argss.getVargs().getFirst());
    }
}