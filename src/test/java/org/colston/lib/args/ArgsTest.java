package org.colston.lib.args;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArgsTest {

    @Test
    void parse() {

        String a = "--cli --to-pdf --romaji-lyrics --filename-suffix=romaji --output-dir=/home/simon/tmp /home/simon/Drive/sanshin/mykunkunshi/kicks/_test.kicks";
        String[] args = a.split(" ");
        Args argss = Args.builder()
                .withVargs(true)
                .parameters(
                        new Param("--cli", "Run on command line without GUI", true),
                        new Param("--to-pdf", "Convert document to pdf", true),
                        new Param("--romaji-lyrics", "Convert the lyrics to romaji", true),
                        new Param("--filename-suffix", "Suffix to add to the output file name", false),
                        new Param("--output-dir", "Output directory", false)
                )
                .parse(args);
        assertTrue(argss.is("--cli"));
        assertTrue(argss.is("--to-pdf"));
        assertTrue(argss.is("--romaji-lyrics"));
        assertEquals("romaji", argss.get("--filename-suffix"));
        assertEquals("/home/simon/tmp", argss.get("--output-dir"));
        assertEquals(1, argss.getVargs().size());
        assertEquals("/home/simon/Drive/sanshin/mykunkunshi/kicks/_test.kicks", argss.getVargs().getFirst());
    }
}