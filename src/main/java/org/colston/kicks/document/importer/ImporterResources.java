package org.colston.kicks.document.importer;

import org.colston.kicks.render.RendererResources;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ImporterResources {

    private static final Set<Character> DIGRAPH_MARKERS = Set.of('イ', 'ロ', '下');
    private static final Map<String, String> KANJI_TO_NUMBERS;
    private static final Map<String, String> NUMBERS_TO_KANJI;
    static {
        KANJI_TO_NUMBERS = new HashMap<>();
        KANJI_TO_NUMBERS.put("０", "00"); // full-width zero is a rest
        for (int string = 1; string <= 3; string++) {
            for (int position = 0; position < 9; position++) {
                String kanji = RendererResources.getNoteText(string, position)
                        .replace('ｲ', 'イ').replace('ﾛ', 'ロ'); // kicksabc uses full-width for input convenience
                String number = String.valueOf(string) + position;
                KANJI_TO_NUMBERS.put(kanji, number);
            }
        }
        NUMBERS_TO_KANJI = KANJI_TO_NUMBERS.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    static boolean isNoteFormatKanjiDigraphMarker(char c) {
        return DIGRAPH_MARKERS.contains(c);
    }
    static boolean isNoteFormatKanjiChar(char ch) {
        return isNoteFormatKanjiDigraphMarker(ch) || KANJI_TO_NUMBERS.containsKey(String.valueOf(ch));
    }

    public static String getNoteFormatKanjiAsNumbers(String kanji) {
        return KANJI_TO_NUMBERS.get(kanji);
    }

    public static String getNoteFormatNumbersAsKanji(String numbers) {
        return NUMBERS_TO_KANJI.get(numbers);
    }

    public static String getNoteFormatNumbersAsKanji(int string, int placement) {
        return getNoteFormatNumbersAsKanji(String.valueOf(string) + placement);
    }
}
