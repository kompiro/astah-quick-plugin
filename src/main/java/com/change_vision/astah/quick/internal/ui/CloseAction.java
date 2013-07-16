package com.change_vision.astah.quick.internal.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URL;

@SuppressWarnings("serial")
public final class CloseAction extends AbstractAction {

    private QuickWindow window;

    public CloseAction(QuickWindow window) {
        URL iconURL = this.getClass().getResource("/icons/glyphicons_207_remove_2.png");
        putValue(Action.SMALL_ICON, new ImageIcon(iconURL));
        this.window = window;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        window.close();
        window.reset();
    }
}
