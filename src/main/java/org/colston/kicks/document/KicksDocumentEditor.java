package org.colston.kicks.document;

import org.colston.lib.i18n.Messages;

import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Used to edit a {@link KicksDocument}.
 * The listeners are added here allowing the document to be changed without having to re-register the listeners.
 */
public class KicksDocumentEditor {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final EventListenerList listeners = new EventListenerList();
    private final Comparator<Locatable> comparator = new LocatableComparator();
    private final Key key = new Key();
    private KicksDocument doc;

    public KicksDocument getDocument() {
        return doc;
    }

    public void setDocument(KicksDocument document) {
        this.doc = document;
    }

    private void add(KicksDocument document) {
        if (!document.getNotes().isEmpty()) {
            int listIndex = Collections.binarySearch(doc.getNotes(), document.getNotes().getFirst(), comparator);
            listIndex = -listIndex - 1;
            doc.getNotes().addAll(listIndex, document.getNotes());
        }
        if (!document.getRepeats().isEmpty()) {
            int listIndex = Collections.binarySearch(doc.getRepeats(), document.getRepeats().getFirst(), comparator);
            listIndex = -listIndex - 1;
            doc.getRepeats().addAll(listIndex, document.getRepeats());
        }
        if (!document.getLyrics().isEmpty()) {
            int listIndex = Collections.binarySearch(doc.getLyrics(), document.getLyrics().getFirst(), comparator);
            listIndex = -listIndex - 1;
            doc.getLyrics().addAll(listIndex, document.getLyrics());
        }
    }

    private void remove(KicksDocument document) {
        doc.getNotes().removeAll(document.getNotes());
        doc.getRepeats().removeAll(document.getRepeats());
        doc.getLyrics().removeAll(document.getLyrics());
    }

    public void setTitle(String newTitle) {
        Song song = doc.getSongs().getFirst();
        UndoableEdit edit = new SetEdit<>(song, song.getTitle(), newTitle, Song::setTitle,
                Messages.get(getClass(), "undo.set.title"));
        song.setTitle(newTitle);
        fireUndoableEditHappened(new UndoableEditEvent(this, edit));
        fireDocumentUpdated();
    }

    public void setTuning(Tuning tuning) {
        Song song = doc.getSongs().getFirst();
        UndoableEdit edit = new SetEdit<>(song, song.getTuning(), tuning, Song::setTuning,
                Messages.get(getClass(), "undo.set.tuning"));
        song.setTuning(tuning);
        fireUndoableEditHappened(new UndoableEditEvent(this, edit));
        fireDocumentUpdated();
    }

    public void updateVersion() {
        String version = dateFormat.format(new Date());
        doc.getProperties().setVersion(version);
    }

    public void setTranscription(String transcription) {
        Properties properties = doc.getProperties();
        UndoableEdit edit = new SetEdit<>(properties, properties.getTranscription(), transcription, Properties::setTranscription,
                Messages.get(getClass(), "undo.set.transcription"));
        properties.setTranscription(transcription);
        fireUndoableEditHappened(new UndoableEditEvent(this, edit));
        fireDocumentUpdated();
    }

    public void addNote(Note note) {
        List<Note> notes = doc.getNotes();
        UndoableEdit edit;
        int listIndex = Collections.binarySearch(notes, note, comparator);
        if (listIndex < 0) {
            listIndex = -listIndex - 1;
            notes.add(listIndex, note);
            edit = new AddEdit<>(note.getIndex(), note.getOffset(), notes, listIndex,
                    Messages.get(getClass(), "undo.add.note"));
        } else {
            Note n = notes.set(listIndex, note);
            edit = new ReplaceEdit<>(note.getIndex(), note.getOffset(), notes, listIndex, n,
                    Messages.get(getClass(), "undo.replace.note"));
        }
        fireUndoableEditHappened(new UndoableEditEvent(this, edit));
        fireDocumentUpdated();
    }

    public void removeNote(int index, int offset) {
        List<Note> notes = doc.getNotes();
        key.index = index;
        key.offset = offset;
        int listIndex = Collections.binarySearch(notes, key, comparator);
        if (listIndex >= 0) {
            Note n = notes.remove(listIndex);
            UndoableEdit edit = new RemoveEdit<>(index, offset, notes, listIndex, n,
                    Messages.get(getClass(), "undo.remove.note"));
            fireUndoableEditHappened(new UndoableEditEvent(this, edit));
            fireDocumentUpdated();
        }
    }

    public void setUtou(int index, int offset, Utou newValue) {
        key.index = index;
        key.offset = offset;
        Note n = findPreviousNote(index, offset);
        if (n != null) {
            UndoableEdit edit;
            if (n.getUtou() == newValue) {
                n.setUtou(Utou.NONE);
                edit = new SetEdit<>(index, offset, n, newValue, Utou.NONE,
                        Note::setUtou, Messages.get(getClass(), "undo.set.utou"));
            } else {
                Utou oldValue = n.getUtou();
                n.setUtou(newValue);
                edit = new SetEdit<>(index, offset, n, oldValue, newValue,
                        Note::setUtou, Messages.get(getClass(), "undo.set.utou"));
            }
            fireUndoableEditHappened(new UndoableEditEvent(this, edit));
            fireDocumentUpdated();
        }
    }

    public void setFlat(int index, int offset) {
        key.index = index;
        key.offset = offset;
        Note n = findPreviousNote(index, offset);
        if (n != null) {
            Accidental oldValue = n.getAccidental();
            Accidental newValue = oldValue == Accidental.FLAT ? Accidental.NONE : Accidental.FLAT;
            n.setAccidental(newValue);
            SetEdit<Note, Accidental> edit = new SetEdit<>(index, offset, n, oldValue, newValue,
                    Note::setAccidental, Messages.get(getClass(), "undo.set.flat"));
            fireUndoableEditHappened(new UndoableEditEvent(this, edit));
            fireDocumentUpdated();
        }
    }

    public void setChord(int index, int offset) {
        List<Note> notes = doc.getNotes();
        key.index = index;
        key.offset = offset;
        int listIndex = Collections.binarySearch(notes, key, comparator);
        if (listIndex >= 0) {
            Note n = notes.get(listIndex);
            boolean oldValue = n.isChord();
            n.setChord(!n.isChord());
            SetEdit<Note, Boolean> edit = new SetEdit<>(index, offset, n, oldValue, !oldValue,
                    Note::setChord, Messages.get(getClass(), "undo.set.chord"));
            fireUndoableEditHappened(new UndoableEditEvent(this, edit));
            fireDocumentUpdated();
        }
    }

    public void setSlur(int index, int offset) {
        List<Note> notes = doc.getNotes();
        key.index = index;
        key.offset = offset;
        int off = Collections.binarySearch(notes, key, comparator);
        if (off >= 0) {
            Note n = notes.get(off);
            boolean oldValue = n.isSlur();
            n.setSlur(!n.isSlur());
            SetEdit<Note, Boolean> edit = new SetEdit<>(index, offset, n, oldValue, !oldValue,
                    Note::setSlur, Messages.get(getClass(), "undo.set.slur"));
            fireUndoableEditHappened(new UndoableEditEvent(this, edit));
            fireDocumentUpdated();
        }
    }

    public void setNoteSize(int index, int offset, boolean value) {
        List<Note> notes = doc.getNotes();
        key.index = index;
        key.offset = offset;
        int off = Collections.binarySearch(notes, key, comparator);
        if (off >= 0) {
            Note n = notes.get(off);
            boolean oldValue = n.isSmall();
            if (value != oldValue) {
                n.setSmall(value);
                SetEdit<Note, Boolean> edit = new SetEdit<>(index, offset, n, oldValue, !oldValue,
                        Note::setSmall, Messages.get(getClass(), "undo.set.note.size"));
                fireUndoableEditHappened(new UndoableEditEvent(this, edit));
                fireDocumentUpdated();
            }
        }
    }

    public void setFinger(int index, int offset, int finger) {
        List<Note> notes = doc.getNotes();
        key.index = index;
        key.offset = offset;
        int off = Collections.binarySearch(notes, key, comparator);
        if (off >= 0) {
            Note n = notes.get(off);
            int oldValue = n.getFinger();
            int newValue = oldValue == finger ? 0 : finger; // same value deletes
            if (newValue != oldValue) {
                n.setFinger(newValue);
                SetEdit<Note, Integer> edit = new SetEdit<>(index, offset, n, oldValue, newValue,
                        Note::setFinger, Messages.get(getClass(), "undo.set.finger"));
                fireUndoableEditHappened(new UndoableEditEvent(this, edit));
                fireDocumentUpdated();
            }
        }
    }

    public void addLyric(Lyric lyric) {
        List<Lyric> lyrics = doc.getLyrics();
        int listIndex = Collections.binarySearch(lyrics, lyric, comparator);
        UndoableEdit edit;
        if (listIndex < 0) {
            listIndex = -listIndex - 1;
            lyrics.add(listIndex, lyric);
            edit = new AddEdit<>(lyric.getIndex(), lyric.getOffset(), lyrics, listIndex,
                    Messages.get(getClass(), "undo.add.lyric"));
        } else {
            Lyric l = lyrics.set(listIndex, lyric);
            edit = new ReplaceEdit<>(lyric.getIndex(), lyric.getOffset(), lyrics, listIndex, l,
                    Messages.get(getClass(), "undo.replace.lyric"));
        }
        fireUndoableEditHappened(new UndoableEditEvent(this, edit));
        fireDocumentUpdated();
    }

    public void removeLyric(int index, int offset) {
        List<Lyric> lyrics = doc.getLyrics();
        key.index = index;
        key.offset = offset;
        int listIndex = Collections.binarySearch(lyrics, key, comparator);
        if (listIndex >= 0) {
            Lyric l = lyrics.remove(listIndex);
            UndoableEdit edit = new RemoveEdit<>(index, offset, lyrics, listIndex, l,
                    Messages.get(getClass(), "undo.remove.lyric"));
            fireUndoableEditHappened(new UndoableEditEvent(this, edit));
            fireDocumentUpdated();
        }
    }

    public void addRepeat(Repeat repeat) {
        List<Repeat> repeats = doc.getRepeats();
        UndoableEdit edit;
        int listIndex = Collections.binarySearch(repeats, repeat, comparator);
        if (listIndex < 0) {
            listIndex = -listIndex - 1;
            repeats.add(listIndex, repeat);
            edit = new AddEdit<>(repeat.getIndex(), repeat.getOffset(), repeats, listIndex,
                    Messages.get(getClass(), "undo.add.repeat"));
        } else {
            Repeat r = repeats.set(listIndex, repeat);
            edit = new ReplaceEdit<>(r.getIndex(), r.getOffset(), repeats, listIndex, r,
                    Messages.get(getClass(), "undo.replace.repeat"));
        }
        fireUndoableEditHappened(new UndoableEditEvent(this, edit));
        fireDocumentUpdated();
    }

    public void removeRepeat(int index, int offset) {
        List<Repeat> repeats = doc.getRepeats();
        key.index = index;
        key.offset = offset;
        int listIndex = Collections.binarySearch(repeats, key, comparator);
        if (listIndex >= 0) {
            Repeat r = repeats.remove(listIndex);
            UndoableEdit edit = new RemoveEdit<>(index, offset, repeats, listIndex, r,
                    Messages.get(getClass(), "undo.remove.repeat"));
            fireUndoableEditHappened(new UndoableEditEvent(this, edit));
            fireDocumentUpdated();
        }
    }

    public KicksDocument copy(LocatableRange range) {
        List<Note> notes = retrieveFromList(doc.getNotes(), range, false);
        List<Repeat> repeats = retrieveFromList(doc.getRepeats(), range, false);
        List<Lyric> lyrics = retrieveFromList(doc.getLyrics(), range, false);
        if (notes.isEmpty() && repeats.isEmpty() && lyrics.isEmpty()) {
            return null;
        }
        KicksDocument kicksDoc = new KicksDocument();
        kicksDoc.getNotes().addAll(notes);
        kicksDoc.getRepeats().addAll(repeats);
        kicksDoc.getLyrics().addAll(lyrics);
        return kicksDoc;
    }

    public void paste(int cursorIndex, int cursorOffset, KicksDocument doc) {
        moveDocumentToNewLocation(cursorIndex, cursorOffset, doc);
        Locatable lowest = findLowest(null, doc.getNotes());
        lowest = findLowest(lowest, doc.getRepeats());
        lowest = findLowest(lowest, doc.getLyrics());
        Locatable highest = findHighest(null, doc.getNotes());
        highest = findHighest(highest, doc.getRepeats());
        highest = findHighest(highest, doc.getLyrics());
        KicksDocument removed = removeRange(new SimpleLocatableRange(lowest.getIndex(), lowest.getOffset(), highest.getIndex(), highest.getOffset()));
        add(doc);
        UndoableEdit edit = new PasteDocumentEdit(cursorIndex, cursorOffset, doc, removed,
                Messages.get(getClass(), "undo.paste"));
        fireUndoableEditHappened(new UndoableEditEvent(this, edit));
        fireDocumentUpdated();
    }

    private void moveDocumentToNewLocation(int index, int offset, KicksDocument doc) {
        Locatable lowest = findLowest(null, doc.getNotes());
        lowest = findLowest(lowest, doc.getRepeats());
        lowest = findLowest(lowest, doc.getLyrics());
        int indexDelta = index - lowest.getIndex();
        int offsetDelta = offset - lowest.getOffset();
        moveLocatables(indexDelta, offsetDelta, doc.getNotes());
        moveLocatables(indexDelta, offsetDelta, doc.getRepeats());
        moveLocatables(indexDelta, offsetDelta, doc.getLyrics());
    }

    private <T extends Locatable> void moveLocatables(int indexDelta, int offsetDelta, List<T> locatables) {
        for (Locatable locatable : locatables) {
            locatable.move(indexDelta, offsetDelta);
        }
    }

    private <T extends Locatable> Locatable findLowest(Locatable lowest, List<T> locatables) {
        if (locatables.isEmpty()) {
            return lowest;
        }
        Locatable current = locatables.getFirst();
        return lowest == null || lowest.isGreaterThan(current) ? current : lowest;
    }

    private <T extends Locatable> Locatable findHighest(Locatable highest, List<T> locatables) {
        if (locatables.isEmpty()) {
            return highest;
        }
        Locatable current = locatables.getLast();
        return highest == null || highest.isLessThan(current) ? current : highest;
    }

    public void remove(LocatableRange range) {
        KicksDocument kicksDoc = removeRange(range);
        if (kicksDoc != null) {
            UndoableEdit edit = new RemoveDocumentEdit(range.getLow().getIndex(), range.getLow().getOffset(), kicksDoc,
                    Messages.get(getClass(), "undo.remove.selection"));
            fireUndoableEditHappened(new UndoableEditEvent(this, edit));
        }
        fireDocumentUpdated();
    }

    public KicksDocument removeRange(LocatableRange range) {
        List<Note> removedNotes = retrieveFromList(doc.getNotes(), range, true);
        List<Repeat> removedRepeats = retrieveFromList(doc.getRepeats(), range, true);
        List<Lyric> removedLyrics = retrieveFromList(doc.getLyrics(), range, true);
        if (!removedNotes.isEmpty()
                || !removedRepeats.isEmpty()
                || !removedLyrics.isEmpty()) {
            KicksDocument kicksDoc = new KicksDocument();
            kicksDoc.getNotes().addAll(removedNotes);
            kicksDoc.getRepeats().addAll(removedRepeats);
            kicksDoc.getLyrics().addAll(removedLyrics);
            return kicksDoc;
        }
        return null;
    }

    public Note findPreviousNote(int index, int offset) {
        List<Note> notes = doc.getNotes();
        if (notes.isEmpty()) {
            return null;
        }
        int listIndex = findListIndex(notes, index, offset, true);
        return listIndex == -1 ? null : notes.get(listIndex);
    }

    private <T extends Locatable> List<T> retrieveFromList(List<T> list, LocatableRange range, boolean remove) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        Locatable start = range.getLow();
        int startIndex = findListIndex(list, start.getIndex(), start.getOffset(), false);
        Locatable end = range.getHigh();
        int endIndex = findListIndex(list, end.getIndex(), end.getOffset(), true);
        List<T> subList = list.subList(startIndex, endIndex + 1);
        if (subList.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<T> removed = new ArrayList<>(subList);
        if (remove) {
            subList.clear();
        }
        return removed;
    }

    private int findListIndex(List<? extends Locatable> list, int index, int offset, boolean previous) {
        key.index = index;
        key.offset = offset;
        int listIndex = Collections.binarySearch(list, key, comparator);
        if (listIndex >= 0) {
            // return the note at this index and offset
            return listIndex;
        } else {
            if (previous && listIndex == -1) {
                // corner case for not found at start of list
                return -1;
            }
            // listIndex = -(insertion point) - 1, so add 2 to get the previous, or 1 for next
            return Math.abs(previous ? listIndex + 2 : listIndex + 1);
        }
    }

    public void addDocumentListener(KicksDocumentListener l) {
        listeners.add(KicksDocumentListener.class, l);
    }

    public void removeDocumentListener(KicksDocumentListener l) {
        listeners.remove(KicksDocumentListener.class, l);
    }

    public void addUndoableEditListener(UndoableEditListener l) {
        listeners.add(UndoableEditListener.class, l);
    }

    public void removeUndoableEditListener(UndoableEditListener l) {
        listeners.remove(UndoableEditListener.class, l);
    }

    protected void fireDocumentUpdated() {
        for (KicksDocumentListener l : listeners.getListeners(KicksDocumentListener.class)) {
            l.documentUpdated();
        }
    }

    protected void fireDocumentUpdated(int index, int offset) {
        for (KicksDocumentListener l : listeners.getListeners(KicksDocumentListener.class)) {
            l.locationUpdated(index, offset);
            l.documentUpdated();
        }
    }

    protected void fireUndoableEditHappened(UndoableEditEvent e) {
        for (UndoableEditListener l : listeners.getListeners(UndoableEditListener.class)) {
            l.undoableEditHappened(e);
        }
    }

    private class AddEdit<T> extends KicksDocumentEdit {
        private final List<T> list;
        private final int listIndex;
        private T element;

        AddEdit(int index, int offset, List<T> list, int listIndex, String presentationName) {
            super(index, offset, presentationName);
            this.list = list;
            this.listIndex = listIndex;
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            element = list.remove(listIndex);
            fireDocumentUpdated(getIndex(), getOffset());
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            list.add(listIndex, element);
            fireDocumentUpdated(getIndex(), getOffset());
        }
    }

    private class ReplaceEdit<T> extends KicksDocumentEdit {
        private final List<T> list;
        private final int listIndex;
        private T element;

        public ReplaceEdit(int index, int offset, List<T> list, int listIndex, T element, String presentationName) {
            super(index, offset, presentationName);
            this.list = list;
            this.listIndex = listIndex;
            this.element = element;
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            element = list.set(listIndex, element);
            fireDocumentUpdated(getIndex(), getOffset());
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            element = list.set(listIndex, element);
            fireDocumentUpdated(getIndex(), getOffset());
        }
    }

    private class RemoveEdit<T> extends KicksDocumentEdit {
        private final List<T> list;
        private final int listIndex;
        private T element;

        public RemoveEdit(int index, int offset, List<T> list, int listIndex, T element, String presentationName) {
            super(index, offset, presentationName);
            this.list = list;
            this.listIndex = listIndex;
            this.element = element;
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            list.add(listIndex, element);
            fireDocumentUpdated(getIndex(), getOffset());
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            element = list.remove(listIndex);
            fireDocumentUpdated(getIndex(), getOffset());
        }
    }

    private class RemoveDocumentEdit extends KicksDocumentEdit {

        private final KicksDocument document;

        public RemoveDocumentEdit(int index, int offset, KicksDocument document, String presentationName) {
            super(index, offset, presentationName);
            this.document = document;
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            add(document);
            fireDocumentUpdated(getIndex(), getOffset());
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            remove(document);
            fireDocumentUpdated();
        }
    }

    private class SetEdit<T, V> extends KicksDocumentEdit {
        private final T element;
        private final V oldValue;
        private final V newValue;
        private final BiConsumer<T, V> consumer;

        public SetEdit(int index, int offset, T element, V oldValue, V newValue, BiConsumer<T, V> c,
                       String presentationName) {
            super(index, offset, presentationName);
            this.element = element;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.consumer = c;
        }

        public SetEdit(T element, V oldValue, V newValue, BiConsumer<T, V> c, String presentationName) {
            super(presentationName);
            this.element = element;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.consumer = c;
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            consumer.accept(element, oldValue);
            if (isUpdateLocation()) {
                fireDocumentUpdated(getIndex(), getOffset());
            } else {
                fireDocumentUpdated();
            }
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            consumer.accept(element, newValue);
            if (isUpdateLocation()) {
                fireDocumentUpdated(getIndex(), getOffset());
            } else {
                fireDocumentUpdated();
            }
        }
    }

    private class PasteDocumentEdit extends KicksDocumentEdit {

        private final KicksDocument added;
        private final KicksDocument removed;

        public PasteDocumentEdit(int index, int offset, KicksDocument added, KicksDocument removed, String presentationName) {
            super(index, offset, presentationName);
            this.added = added;
            this.removed = removed;
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            remove(added);
            if (removed != null) {
                add(removed);
            }
            fireDocumentUpdated(getIndex(), getOffset());
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            if (removed != null) {
                remove(removed);
            }
            add(added);
            fireDocumentUpdated();
        }
    }


    private static class Key implements Locatable {
        private int index;
        private int offset;

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public int getOffset() {
            return offset;
        }
    }
}
