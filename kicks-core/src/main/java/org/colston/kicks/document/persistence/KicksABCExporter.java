package org.colston.kicks.document.persistence;

import org.colston.kicks.document.*;
import org.colston.utils.KanaConverter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KicksABCExporter {

    //config
    // true if the notes are specified by kanji, false if they are specified by numbers
    private final boolean noteFormatKanji;

    public KicksABCExporter() {
        this(true);
    }

    public KicksABCExporter(boolean noteFormatKanji) {
        this.noteFormatKanji = noteFormatKanji;
    }

    void save(KicksDocument doc, BufferedWriter writer) throws IOException {
        for (int songIndex = 0; songIndex < doc.getSongs().size(); songIndex++) {
            Song song = doc.getSongs().get(songIndex);

            writeHeader(writer, song);
            LocatableRange songRange = calcSongRange(doc, songIndex);
            writeNotesAndRepeats(doc, writer, songRange);
            writeLyrics(doc, writer, songRange);

            writer.newLine();
        }
    }

    private void writeLyrics(KicksDocument doc, BufferedWriter writer, LocatableRange songRange) throws IOException {
        // initial space from first note to first lyric, probably sanshin intro
        Locatable prev = null;
        Iterator<Note> iterator = doc.getNotes(songRange).iterator();
        if (iterator.hasNext()) {
            prev = iterator.next();
            prev = new SimpleLocatable(prev.getIndex() -1 , prev.getOffset());
        }
        for (Lyric lyric : doc.getLyrics(songRange)) {
            if (prev != null) {
                for (int i = prev.getIndex(); i < lyric.getIndex() - 1; i++) {
                    writer.write(" *");
                }
                if (prev.getIndex() != lyric.getIndex() && prev.getOffset() < 6 && lyric.getOffset() == Locatable.CELL_TICKS) {
                    writer.write(" *");
                }
                if (prev.getIndex() != lyric.getIndex() && lyric.getOffset() > 6) {
                    writer.write(" *");
                }
            }
            String absoluteOffset = "";
            if (lyric.getOffset() == Locatable.CELL_TICKS) {
                writer.write(".");
            } else {
                writer.write(" ");
                if (lyric.getOffset() != Locatable.CELL_TICKS / 2) {
                    absoluteOffset = "<" + lyric.getOffset() + ">";
                }
            }
            writer.write(KanaConverter.toRomaji(lyric.getValue()));
            writer.write(absoluteOffset);
            prev = lyric;
        }
        writer.newLine();
    }

    private void writeNotesAndRepeats(KicksDocument doc, BufferedWriter writer, LocatableRange songRange) throws IOException {
        List<Repeat> songRepeats = extractSongRepeats(doc, songRange);
        int repeatIndex = songRepeats.isEmpty() ? -1 : 0;

        boolean chordStarted = false;
        for (Note note : doc.getNotes(songRange)) {
            Repeat repeat = getRepeat(songRepeats, repeatIndex);
            while (repeat != null && note.isGreaterThan(repeat)) {
                // while loop to handle consecutive repeats
                writer.write(repeat.isBack() ? " ]" : " [");
                switch (repeat.getStyle()) {
                    case TRIANGLE_OUTLINE -> writer.write("t");
                    case CIRCLE_FILLED ->  writer.write("C");
                    case CIRCLE_OUTLINE -> writer.write("c");
                }
                repeatIndex = calcRepeatIndex(songRepeats, repeatIndex);
                repeat = getRepeat(songRepeats, repeatIndex);
            }
            if (note.isChord()) {
                if (!chordStarted) {
                    writer.write(" {");
                    chordStarted = true;
                }
            }
            String absoluteOffset = "";
            if (chordStarted) {  // absolute positioning not valid for chord
                writer.write(" ");
            } else {
                if (note.getOffset() == Locatable.CELL_TICKS) {
                    writer.write(".");
                } else {
                    writer.write(" ");
                    if (note.getOffset() != Locatable.CELL_TICKS / 2) {
                        absoluteOffset = "<" + note.getOffset() + ">";
                    }
                }
            }
            if (!noteFormatKanji) {
                writer.write(String.valueOf(note.getString()));
                writer.write(String.valueOf(note.getPlacement()));
            } else {
                String kanji = KicksABCResources.getNoteFormatNumbersAsKanji(note.getString(), note.getPlacement());
                writer.write(kanji);
            }
            writer.write(absoluteOffset);
            if (!chordStarted) {
                if (note.isSmall()) {
                    writer.write("s");
                }
                if (note.getAccidental() == Accidental.FLAT) {
                    writer.write("b");
                }
                switch (note.getUtou()) {
                    case KAKI ->  writer.write("^");
                    case UCHI ->  writer.write("'");
                }
                if (note.getFinger() > 0) {
                    writer.write("f" + note.getFinger());
                }
                if (note.isSlur()) {
                    writer.write(")");
                }
            }
            if (!note.isChord() && chordStarted) {
                writer.write(" }");
                chordStarted = false;
            }
        }
        // there might be a repeat after the notes
        Repeat repeat = getRepeat(songRepeats, repeatIndex);
        if (repeat != null) {
            writer.write(repeat.isBack() ? " ]" : " [");
        }
        writer.newLine();
    }

    private void writeHeader(BufferedWriter writer, Song song) throws IOException {
        writer.write("T:" + song.getTitle());
        writer.newLine();
        if (song.getTitleRomaji() != null) {
            writer.write("E:" + song.getTitleRomaji());
            writer.newLine();
        }
        writer.write("K:" + convertTuning(song.getTuning()));
        writer.newLine();
        if (song.getTempo() != null) {
            writer.write(" Q:" + song.getTempo());
            writer.newLine();
        }
        if (noteFormatKanji) {
            writer.write("I:note-format kanji");
            writer.newLine();
        }
        writer.newLine();
    }

    private List<Repeat> extractSongRepeats(KicksDocument doc, LocatableRange songRange) {
        List<Repeat> repeats = new ArrayList<>();
        doc.getRepeats(songRange).forEach(repeats::add);
        return repeats;
    }

    private static LocatableRange calcSongRange(KicksDocument doc, int songIndex) {
        Song song = doc.getSongs().get(songIndex);
        LocatableRange lr;
        if (songIndex + 1 == doc.getSongs().size()) {
            // top of range is very high number (that does not overflow when calculating ticks!!)
            lr = new SimpleLocatableRange(song.getIndex(), 0, Integer.MAX_VALUE / 24, 12);
        } else {
            Song nextSong = doc.getSongs().get(songIndex + 1);
            lr = new SimpleLocatableRange(song.getIndex(), 0, nextSong.getIndex(), 1);
        }
        return lr;
    }

    private Repeat getRepeat(List<Repeat> repeats, int repeatIndex) {
        return repeatIndex >=0 ? repeats.get(repeatIndex) : null;
    }

    private int calcRepeatIndex(List<Repeat> repeats, int next) {
        if (next < 0) {
            return -1;
        }
        next = next + 1;
        return next == repeats.size() ? -1 : next;
    }

    private String convertTuning(Tuning tuning) {
        switch (tuning) {
            case HONCHOUSHI -> {
                return "honchoshi";
            }
            case SANSAGE ->  {
                return "sansage";
            }
            case NIAGE ->   {
                return "niage";
            }
        }
        return "honchoshi";
    }
}
