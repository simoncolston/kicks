package org.colston.kicks.document.importer;

import org.colston.kicks.document.Accidental;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.Locatable;
import org.colston.kicks.document.Lyric;
import org.colston.kicks.document.Note;
import org.colston.kicks.document.Repeat;
import org.colston.kicks.document.SimpleLocatable;
import org.colston.kicks.document.Song;
import org.colston.kicks.document.Tuning;
import org.colston.kicks.document.Utou;
import org.colston.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.Character.isDigit;

public class KicksABCImporter implements Importer {

    private final Logger log = Logger.getLogger(KicksABCImporter.class.getName());
    private KicksDocument doc;

    // state
    // index and offset within the kicks document being generated
    private int docIndex = 0;
    private int docOffset = 0;
    // indexes used to align the lyrics with the notes in the previous line
    private int noteLineStartDocIndex;  // docIndex when starting a note line from the abc file
    private int noteLineEndDocIndex;    // docIndex when ending a note line from the abc file
    // validation that lyric line follows note line
    private boolean previousLineWasNote = true;
    // When not null indicates we're parsing a chord.  Chord notes are stored in this list.
    private List<Note> chordNotes = null;
    // Indicates that the previous object was a repeat start that needs to be added with the next note
    private boolean repeatStart = false;
    // Indicates we are processing a song header
    private boolean header;
    // Index of the song we are currently parsing
    private int songIndex;

    // logging/error messages
    private int abcScriptLineNumber = 0;

    private int calcIndex(int o) {
        // if we are currently processing a chord we don't move the index on
        if (chordNotes != null) {
            return docIndex;
        }
        if (docOffset != 0 && o <= docOffset) {
            // must have moved on to the next cell
            docIndex++;
        }
        docOffset = o;
        return docIndex;
    }

    private void incrementIndex(int newOffset) {
        docIndex++;
        docOffset = newOffset;
    }

    private void startNotesSetIndex() {
        noteLineStartDocIndex = docIndex;
    }

    private void endNotesSetIndex() {
        noteLineEndDocIndex = docIndex;
    }

    private void startLyricsSetIndex() {
        docIndex = noteLineStartDocIndex;
        docOffset = 0;
    }

    private void endLyricsSetIndex() {
        docIndex = noteLineEndDocIndex;
    }

    private int calcOffset(StringBuilder s, int defaultValue) throws Exception {
        int lt = s.indexOf("<");
        if (lt < 0) {
            return defaultValue;
        }
        int gt = s.indexOf(">");
        if (gt < 0) {
            raiseException("Invalid definition: " + s);
        }
        int o = Integer.parseInt(s.substring(lt + 1, gt));
        if (o < 0 || o > 12) {
            raiseException("Invalid absolute offset: " + s);
        }
        s.delete(lt, gt + 1);
        return o;
    }

    @Override
    public KicksDocument importFile(File file) throws Exception {
        log.info("Importing file: " + file.getAbsolutePath());
        doc = new KicksDocument();
        songIndex = -1;
        try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = br.readLine()) != null) {
                abcScriptLineNumber++;
                line = line.trim();
                if (line.startsWith("%")) {
                    // comment
                    continue;
                }
                if (parseCommand(line)) {
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }
                parseNotesOrLyrics(line);
            }
        }
        log.info("Import complete.");
        return doc;
    }

    private void parseNotesOrLyrics(String line) throws Exception {
        if (line.length() < 2) {
            raiseException("Line is too short: " + line);
        }
        char ch = line.charAt(0);
        if (ch == '.') {
            ch = line.charAt(1);
        }
        if (ch == '[' || ch == ']' || isDigit(ch)) {
            parseNotes(line);
            previousLineWasNote = true;
        } else if (Character.isLetter(ch) || ch == '*') {
            if (!previousLineWasNote) {
                raiseException("Lyric line must be preceded by note line: " + line);
            }
            parseLyrics(line);
            previousLineWasNote = false;
        } else {
            raiseException("Illegal character: " + ch);
        }
    }

    private void parseLyrics(String line) throws Exception {
        startLyricsSetIndex();
        String[] abcls = line.split(" +"); // allow one or more spaces between lyrics
        for (String abcl : abcls) {
            int dot = abcl.indexOf('.');
            if (dot == -1) {
                parseLyric(abcl);
            } else {
                // dot is an abbreviation for <12>
                if (dot > 0) {
                    parseLyric(abcl.substring(0, dot));
                }
                parseLyric(abcl.substring(dot + 1) + "<12>");
            }
        }
        endLyricsSetIndex();
    }

    private void parseLyric(String s) throws Exception {
        StringBuilder abcl = new StringBuilder(s);
        int ch = abcl.charAt(0);
        if (ch == '*') {
            // space, so move on...
            incrementIndex(6);
        } else  {
            int o = calcOffset(abcl, 6);
            int i = calcIndex(o);
            String syllable = Utils.toKatakana(abcl.toString());
            Lyric lyric = new Lyric(i, o, syllable);
            doc.getLyrics().add(lyric);
        }
    }

    private void parseNotes(String line) throws Exception {
        boolean first = true;
        String[] abcns = line.split(" +"); // allow one or more spaces between notes
        for (String abcn : abcns) {
            int dot = abcn.indexOf('.');
            if (dot == -1) {
                parseNote(abcn);
            } else {
                // dot is an abbreviation for <12>
                if (dot > 0) {
                    parseNote(abcn.substring(0, dot));
                }
                parseNote(abcn.substring(dot + 1) + "<12>");
            }
            if (first) {
                first = false;
                startNotesSetIndex();
            }
        }
        endNotesSetIndex();
    }

    private void parseNote(String s) throws Exception {
        StringBuilder abcn = new StringBuilder(s);
        char ch = abcn.charAt(0);
        if (ch == '[') {
            // repeat start
            repeatStart(s, abcn);
        } else if (ch == ']') {
            // repeat end
            repeatEnd(s, abcn);
        } else if (ch == '{') {
            // chord start
            chordStart();
        } else if (ch == '}') {
            // chord end
            chordEnd();
        } else if (isDigit(ch) && isDigit(abcn.charAt(1))) {
            // a note
            int o = calcOffset(abcn, 6);
            int i = calcIndex(o);
            int string = Integer.parseInt(abcn.substring(0, 1));
            if (string < 0 || string > 3) {
                raiseException("Invalid string number: " + string);
            }
            int placement = Integer.parseInt(abcn.substring(1, 2));
            if (placement < 0 || placement > 9) {
                raiseException("Invalid string placement number: " + placement);
            }
            Note note = new Note(i, o, string, placement);
            // note modifiers
            if (o == 12) {
                // default to small
                note.setSmall(true);
            }
            for (int j = 2; j < abcn.length(); j++) {
                switch (abcn.charAt(j)) {
                    case 'l':
                        note.setSmall(false);
                        break;
                    case 's':
                        note.setSmall(true);
                        break;
                    case '\'':
                        note.setUtou(Utou.UCHI);
                        break;
                    case '^':
                        note.setUtou(Utou.KAKI);
                        break;
                    case 'b':
                        note.setAccidental(Accidental.FLAT);
                        break;
                    case 'f':
                        if (!isDigit(abcn.charAt(j + 1))) {
                            raiseException("Invalid finger specification: " + abcn);
                        }
                        int f = Integer.parseInt(abcn.substring(j + 1, j + 2));
                        if (f < 1 || f > 4) {
                            raiseException("Invalid finger specification: " + abcn);
                        }
                        note.setFinger(f);
                        j++; // move on because this is a 2 character modifier
                        break;
                    case ')':
                        note.setSlur(true);
                        break;
                    default:
                        raiseException("Invalid note: " + s);
                }
            }
            if (chordNotes != null) {
                // currently processing a chord so store up the notes until the chord is finished
                chordNotes.add(note);
                if (chordNotes.size() > 3) {
                    raiseException("Too many notes in chord, max. 3");
                }
            } else {
                doc.getNotes().add(note);
            }
            if (repeatStart) {
                repeatStart = false;
                SimpleLocatable l = new SimpleLocatable(i, o);
                l.move(0, -4);
                Repeat repeat = new Repeat(l.getIndex(), l.getOffset(), false);
                doc.getRepeats().add(repeat);
            }
        } else {
            raiseException("Illegal character: " + ch);
        }
    }

    private void chordEnd() {
        if (chordNotes.size() == 1) {
            // not a chord, but allow the bad syntax
            doc.getNotes().add(chordNotes.getFirst());
        } else if (chordNotes.size() == 2) {
            Note n = chordNotes.getFirst();
            Note n1 = new Note(n.getIndex(), 3, n.getString(), n.getPlacement());
            n1.setSmall(true);
            n1.setChord(true);
            doc.getNotes().add(n1);
            n = chordNotes.get(1);
            Note n2 = new Note(n1.getIndex(), 9, n.getString(), n.getPlacement());
            n2.setSmall(true);
            doc.getNotes().add(n2);
        } else if (chordNotes.size() == 3) {
            Note n = chordNotes.getFirst();
            Note n1 = new Note(n.getIndex(), 2, n.getString(), n.getPlacement());
            n1.setSmall(true);
            n1.setChord(true);
            doc.getNotes().add(n1);

            n = chordNotes.get(1);
            Note n2 = new Note(n1.getIndex(), 6, n.getString(), n.getPlacement());
            n2.setSmall(true);
            n2.setChord(true);
            doc.getNotes().add(n2);

            n = chordNotes.get(2);
            Note n3 = new Note(n1.getIndex(), 10, n.getString(), n.getPlacement());
            n3.setSmall(true);
            doc.getNotes().add(n3);
        }
        // now the chord is complete we move the index on...
        incrementIndex(0);
        chordNotes = null;
    }

    private void chordStart() {
        chordNotes = new ArrayList<>();
        if (docOffset > 0) {
            // only increment if we are not at the start of an empty cell
            incrementIndex(0);
        }
    }

    private void repeatEnd(String s, StringBuilder abcn) throws Exception {
        int o;
        int i;
        if (s.contains("<")) {
            // absolute positioning
            o = calcOffset(abcn, 10);
            i = calcIndex(o);
        } else {
            Locatable lastNote = doc.getNotes().getLast();
            SimpleLocatable l = new SimpleLocatable(lastNote);
            l.move(0, lastNote.getOffset() == Locatable.CELL_TICKS ? 3 : 4);
            i = l.getIndex();
            o = l.getOffset();
        }
        Repeat repeat = new Repeat(i, o, true);
        doc.getRepeats().add(repeat);
    }

    private void repeatStart(String s, StringBuilder abcn) throws Exception {
        if (s.contains("<")) {
            // absolute positioning
            int o = calcOffset(abcn, 2);
            int i = calcIndex(o);
            Repeat repeat = new Repeat(i, o, false);
            doc.getRepeats().add(repeat);
        } else {
            repeatStart = true;
        }
    }

    private boolean parseCommand(String line) throws Exception {
        if (line.isEmpty()) {
            if (header) {
                //end of header
                header = false;
                return true;
            }
            return false;
        }
        if (header && line.length() < 2) {
            raiseException("Line is too short: " + line);
        }
        if (line.charAt(1) != ':') {
            if (header) {
                raiseException("Illegal header line: " + line);
            }
            return false;
        }
        switch (line.charAt(0)) {
            case 'T':
                songIndex++;
                int cellsPerColumn = doc.getProperties().getLayout().getCellsPerColumn();
                int cellInCol = docIndex % cellsPerColumn;
                int i = cellInCol == 0 ? docIndex : docIndex + cellsPerColumn - cellInCol;
                docIndex = i + doc.getProperties().getLayout().getCellsPerColumn();
                docOffset = 0;
                Song song = new Song(i);
                song.setTitle(line.substring(2));
                doc.getSongs().add(song);
                header = true;
                break;
            case 'E':
                doc.getSongs().get(songIndex).setTitleRomaji(line.substring(2));
                break;
            case 'K':
                String tuning = line.substring(2).trim();
                switch (tuning) {
                    case "honchoshi":
                        doc.getSongs().get(songIndex).setTuning(Tuning.HONCHOUSHI);
                        break;
                    case "sansage":
                        doc.getSongs().get(songIndex).setTuning(Tuning.SANSAGE);
                        break;
                    case "niage":
                        doc.getSongs().get(songIndex).setTuning(Tuning.NIAGE);
                        break;
                    default:
                        raiseException("Illegal tuning: " + tuning);
                }
                break;
            case 'Q':
                doc.getSongs().get(songIndex).setTempo(line.substring(2, 2 + Math.min(3, line.length() - 2)));
                break;
            default:
                if (header) {
                    raiseException("Illegal header line: " + line);
                }
                return false;
        }
        return true;
    }

    private void raiseException(String message) throws Exception {
        throw new Exception("Line: " + abcScriptLineNumber + ", " + message);
    }
}
