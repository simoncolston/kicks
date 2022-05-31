package org.colston.kicks.gui.util;

import org.colston.kicks.KicksApp;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.im.InputContext;
import java.util.Locale;

public class JapaneseTextFocusListener implements FocusListener {

    @Override
    public void focusGained(FocusEvent e) {
        InputContext ic = e.getComponent().getInputContext();
        ic.selectInputMethod(Locale.JAPAN);
        ic.setCharacterSubsets(KicksApp.settings().getCharacterSubset());
    }

    @Override
    public void focusLost(FocusEvent e) {
    }
}
