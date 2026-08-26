package org.colston.utils;

import com.moji4j.MojiConverter;
import java.util.Map;

public class KanaConverter {
    private static final MojiConverter mojiConverter = new MojiConverter();
    private static final Map<String, String> ROMAFIX;
    private static final Map<String, String> ROMAFIX_REVERSE;

    static {
        ROMAFIX = Map.ofEntries(
                Map.entry("dhi", "di"),
                Map.entry("thi", "ti"),
                Map.entry("toxu", "tu"),
                Map.entry("doxu", "du"),
                Map.entry("kuxi", "kwi"),
                Map.entry("uxe", "ye"),
                Map.entry("dji", "ji"),
                Map.entry("yoxu", "yo"),
                Map.entry("woxu", "u"),
                Map.entry("jyo", "jo"),
                Map.entry("jyu", "ju"),
                Map.entry("wi", "i"),
                Map.entry("yaxu", "yaw"),
                Map.entry("guxwa", "gwa"));
        ROMAFIX_REVERSE = Map.ofEntries(
                Map.entry("di", "dhi"),
                Map.entry("ti", "thi"),
                Map.entry("tu", "toxu"),
                Map.entry("du", "doxu"),
                Map.entry("kwi", "kuxi"),
                Map.entry("ye", "uxe"),
                Map.entry("yaw", "yaxu"),
                Map.entry("gwa", "guxwa"));
    }

    public static String toRomaji(String text) {
        String romaji = mojiConverter.convertKanaToRomaji(text);
        return ROMAFIX.getOrDefault(romaji, romaji);
    }

    public static String toKatakana(String text) {
        return mojiConverter.convertRomajiToKatakana(ROMAFIX_REVERSE.getOrDefault(text, text));
    }
}
