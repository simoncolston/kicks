package org.colston.kicks.gui.canvas;

import org.colston.gui.actions.ActionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create and manipulate the actions required by the canvas.
 *
 * @author simon
 */
final class CanvasActions {
    private static final Map<String, CanvasAction> actions;

    /*
     * The KeyStokes and Action Commands are collected together here because they are all in the same namespace.
     * It will be easier to spot clashes and give all the actions sensible values.
     */
    static {
        Map<String, CanvasAction> map = new LinkedHashMap<>();

        //Notes
        // --- low string
        int string = 1;
        int placement = 0;
        map.put("canvas.note.合", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.合", string, placement++));
        map.put("canvas.note.乙", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_X, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.乙", string, placement++));
        map.put("canvas.note.老", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_C, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.老", string, placement++));
        map.put("canvas.note.下老", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_V, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.下老", string, placement++));
        map.put("canvas.note.ﾛ上", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_B, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.ﾛ上", string, placement++));
        map.put("canvas.note.ﾛ中", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_N, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.ﾛ中", string, placement++));
        map.put("canvas.note.ﾛ尺", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_M, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.ﾛ尺", string, placement++));
        map.put("canvas.note.ｲ合", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.ｲ合", string, placement++));
        map.put("canvas.note.ｲ乙", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.ｲ乙", string, placement));

        // --- middle string
        string = 2;
        placement = 0;
        map.put("canvas.note.四", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_A, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.四", string, placement++));
        map.put("canvas.note.上", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_S, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.上", string, placement++));
        map.put("canvas.note.中", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_D, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.中", string, placement++));
        map.put("canvas.note.尺", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_F, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.尺", string, placement++));
        map.put("canvas.note.下尺", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_G, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.下尺", string, placement++));
        map.put("canvas.note.ﾛ五", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_H, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.ﾛ五", string, placement++));
        map.put("canvas.note.ｲ老", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_J, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.ｲ老", string, placement++));
        map.put("canvas.note.ｲ四", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_K, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.ｲ四", string, placement++));
        map.put("canvas.note.ｲ上", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_L, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.ｲ上", string, placement));

        // --- high string
        string = 3;
        placement = 0;
        map.put("canvas.note.工", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.工", string, placement++));
        map.put("canvas.note.五", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_W, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.五", string, placement++));
        map.put("canvas.note.六", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_E, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.六", string, placement++));
        map.put("canvas.note.七", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_R, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.七", string, placement++));
        map.put("canvas.note.八", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_T, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.八", string, placement++));
        map.put("canvas.note.九", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_Y, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.九", string, placement++));
        map.put("canvas.note.ｲ尺", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_U, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.ｲ尺", string, placement++));
        map.put("canvas.note.ｲ工", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_I, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.ｲ工", string, placement++));
        map.put("canvas.note.ｲ五", new NoteAction(new KeyStroke[]
                {
                        KeyStroke.getKeyStroke(KeyEvent.VK_O, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.SHIFT_DOWN_MASK)
                }, "canvas.note.ｲ五", string, placement));

        // --- rest
        map.put("canvas.rest", new CanvasAction((c, e) -> c.addRest(),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0)}, "canvas.rest"));

        // Decorate notes
        map.put("canvas.chord", new CanvasAction((c, e) -> c.setChord(),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0)}, "canvas.chord"));
        map.put("canvas.slur", new CanvasAction((c, e) -> c.setSlur(),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, 0)}, "canvas.slur"));
        map.put("canvas.flat", new CanvasAction((c, e) -> c.setFlat(),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_0, 0)}, "canvas.flat"));
        map.put("canvas.utou", new CanvasAction((c, e) -> c.setUtou((e.getModifiers() & ActionEvent.SHIFT_MASK) > 0),
                new KeyStroke[]
                        {
                                KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0),
                                KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.SHIFT_DOWN_MASK)
                        }, "canvas.utou"));

        // Edit commands
        map.put("canvas.delete", new CanvasAction((c, e) -> c.delete(),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)}, "canvas.delete"));
        map.put("canvas.backspace", new CanvasAction((c, e) -> c.backspace(),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0)}, "canvas.backspace"));

        // Cursor movement and control
        map.put("canvas.cursor.left", new CanvasAction((c, e) -> c.moveCursorLeft(),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)}, "canvas.cursor.left"));
        map.put("canvas.cursor.right", new CanvasAction((c, e) -> c.moveCursorRight(),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0)}, "canvas.cursor.right"));
        map.put("canvas.cursor.up", new CanvasAction((c, e) -> c.moveCursorUp(e.getModifiers()),
                new KeyStroke[]
                        {
                                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                                KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK),
                                KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK)
                        }, "canvas.cursor.up"));
        map.put("canvas.cursor.down", new CanvasAction((c, e) -> c.moveCursorDown(e.getModifiers()),
                new KeyStroke[]
                        {
                                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK),
                                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK)
                        }, "canvas.cursor.down"));
        map.put("canvas.cursor.auto.off", new CanvasAction((c, e) -> c.setAutoCursor(Canvas.AutoCursor.OFF),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.CTRL_DOWN_MASK)},
                "canvas.cursor.auto.off"));
        map.put("canvas.cursor.auto.half", new CanvasAction((c, e) -> c.setAutoCursor(Canvas.AutoCursor.HALF),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.SHIFT_DOWN_MASK)},
                "canvas.cursor.auto.half"));
        map.put("canvas.cursor.auto.one", new CanvasAction((c, e) -> c.setAutoCursor(Canvas.AutoCursor.ONE),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0)},
                "canvas.cursor.auto.one"));

        // Add other things like repeats
        map.put("canvas.repeatstart", new CanvasAction((c, e) -> c.addRepeat(false),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, 0)},
                "canvas.repeatstart"));
        map.put("canvas.repeatend", new CanvasAction((c, e) -> c.addRepeat(true),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, 0)},
                "canvas.repeatend"));

        // Lyric text input specialised actions
        map.put("canvastext.enter", new CanvasAction((c, e) -> c.addLyric(),
                new KeyStroke[]{KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)},
                "canvastext.enter"));

        actions = Collections.unmodifiableMap(map);
    }

    static void initialise(CanvasControl control) {
        actions.values().forEach(ca -> {
            ActionManager.initialiseResources(ca);
            ca.setControl(control);
        });
    }

    static void addPrefixToInputActionMaps(JComponent component, String prefix) {
        for (String name : actions.keySet()) {
            if (name.startsWith(prefix)) {
                addToInputActionMaps(component, name);
            }
        }
    }

    static void addToInputActionMaps(JComponent component, String actionName) {
        addToInputActionMaps(component, actions.get(actionName));
    }

    @SuppressWarnings("SameParameterValue")
    static List<CanvasAction> getActionsWithPrefix(String prefix) {
        return actions.values().stream().filter(
                e -> e.getActionCommand().startsWith(prefix)).collect(Collectors.toList());
    }

    static CanvasAction getAction(String name) {
        return actions.get(name);
    }

    private static void addToInputActionMaps(JComponent component, CanvasAction ca) {
        for (KeyStroke ks : ca.getKeyStrokes()) {
            component.getInputMap().put(ks, ca.getActionCommand());
        }
        component.getActionMap().put(ca.getActionCommand(), ca);
    }

    static void disableAll() {
        actions.values().forEach(a -> a.setEnabled(false));
    }

    static void enableAll() {
        actions.values().forEach(a -> a.setEnabled(true));
    }
}
