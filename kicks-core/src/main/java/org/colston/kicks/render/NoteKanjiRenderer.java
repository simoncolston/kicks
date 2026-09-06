package org.colston.kicks.render;

import org.colston.kicks.document.Note;

import java.awt.Graphics2D;

public interface NoteKanjiRenderer {

    /**
     * Render the note kanji.
     * Note that the x,y provided here should be the middle of the vertical dimension of the kanji.
     * @param g2 graphics to render to
     * @param n the note to render
     * @param x left hand side of the cell tick
     * @param y y position of the tick
     */
    void render(Graphics2D g2, Note n, int x, int y);
}
