package org.colston.kicks.document;

import jakarta.xml.bind.annotation.*;
import org.colston.sclib.i18n.Messages;

import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "kunkunshi")
@XmlType(
        propOrder =
                {
                        "properties",
                        "songs",
                        "notes",
                        "breaks",
                        "repeats",
                        "lyrics"
                })
public class KicksDocument {
    private final EventListenerList listeners = new EventListenerList();
    private final Comparator<Locatable> comparator = new LocatableComparator();
    private final Key key = new Key();

    @XmlAttribute(required = true)
    private final int version = 1;

    @XmlElement(required = true)
    private final Properties properties = new Properties();

    @XmlElementWrapper(name = "songs", required = true)
    @XmlElement(name = "song")
    private final List<Song> songs = new ArrayList<>();

    @XmlElementWrapper(name = "notes")
    @XmlElement(name = "note")
    private final List<Note> notes = new ArrayList<>();

    @XmlElementWrapper(name = "breaks")
    @XmlElement(name = "break")
    private final List<Break> breaks = new ArrayList<>();

    @XmlElementWrapper(name = "repeats")
    @XmlElement(name = "repeat")
    private final List<Repeat> repeats = new ArrayList<>();

    @XmlElementWrapper(name = "lyrics")
    @XmlElement(name = "lyric")
    private final List<Lyric> lyrics = new ArrayList<>();

    public KicksDocument() {
        songs.add(new Song(0));
    }

    public Properties getProperties() {
        return properties;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public List<Break> getBreaks() {
        return breaks;
    }

    public List<Repeat> getRepeats() {
        return repeats;
    }

    public List<Lyric> getLyrics() {
        return lyrics;
    }

    public String getTitle() {
        Song song = songs.get(0);
        return song.getTitle();
    }

    public void setTitle(String newTitle) {
        Song song = songs.get(0);
        UndoableEdit edit = new SetEdit<>(song, song.getTitle(), newTitle, Song::setTitle,
                Messages.get(getClass(), "undo.set.title"));
        song.setTitle(newTitle);
        fireUndoableEditHappened(new UndoableEditEvent(this, edit));
        fireDocumentUpdated();
    }

    public Tuning getTuning() {
        return songs.get(0).getTuning();
    }

    public void setTuning(Tuning tuning) {
        Song song = songs.get(0);
        UndoableEdit edit = new SetEdit<>(song, song.getTuning(), tuning, Song::setTuning,
                Messages.get(getClass(), "undo.set.tuning"));
        song.setTuning(tuning);
        fireUndoableEditHappened(new UndoableEditEvent(this, edit));
        fireDocumentUpdated();
    }

    public void addNote(Note note) {
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
        int listIndex = Collections.binarySearch(notes, key, comparator);
        if (listIndex >= 0) {
            UndoableEdit edit;
            Note n = notes.get(listIndex);
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
        int listIndex = Collections.binarySearch(notes, key, comparator);
        if (listIndex >= 0) {
            Note n = notes.get(listIndex);
            Accidental oldValue = n.getAccidental();
            Accidental newValue = oldValue == Accidental.FLAT ? Accidental.NONE : Accidental.FLAT;
            n.setAccidental(newValue);
            SetEdit<Note, Accidental> edit = new SetEdit<>(index, offset, n, oldValue, newValue,
                    Note::setAccidental, Messages.get(getClass(), "undo.set.utou"));
            fireUndoableEditHappened(new UndoableEditEvent(this, edit));
            fireDocumentUpdated();
        }
    }

    public void setChord(int index, int offset) {
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

    public void addLyric(Lyric lyric) {
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

    public Lyric getLyric(int index, int offset) {
        key.index = index;
        key.offset = offset;
        int off = Collections.binarySearch(lyrics, key, comparator);
        return off >= 0 ? lyrics.get(off) : null;
    }

    public void addRepeat(Repeat repeat) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + breaks.hashCode();
        result = prime * result + lyrics.hashCode();
        result = prime * result + notes.hashCode();
        result = prime * result + properties.hashCode();
        result = prime * result + repeats.hashCode();
        result = prime * result + songs.hashCode();
        result = prime * result + version;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KicksDocument other = (KicksDocument) obj;
        if (!breaks.equals(other.breaks))
            return false;
        if (!lyrics.equals(other.lyrics))
            return false;
        if (!notes.equals(other.notes))
            return false;
        if (!properties.equals(other.properties))
            return false;
        if (!repeats.equals(other.repeats))
            return false;
        return songs.equals(other.songs);
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

    private static class LocatableComparator implements Comparator<Locatable> {
        @Override
        public int compare(Locatable o1, Locatable o2) {
            return o1.getIndex() == o2.getIndex() ? o1.getOffset() - o2.getOffset() : o1.getIndex() - o2.getIndex();
        }
    }
}
