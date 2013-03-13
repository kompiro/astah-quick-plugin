package com.change_vision.astah.quick.internal.ui.configure;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JWindow;

@SuppressWarnings("serial")
class CancelAction extends AbstractAction {
    final private JWindow window;

    CancelAction(JWindow window) {
        super("cancel");
        this.window = window;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        window.setVisible(false);
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSED));
    }
}