package com.change_vision.astah.quick.internal.ui.configure;

import com.change_vision.astah.quick.internal.Messages;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class ConfigWindow extends JDialog {

    public ConfigWindow(Window window) {
        super(window);
        JPanel configPanel = new ConfigPanel(this);
        add(configPanel);
        pack();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setTitle(Messages.getString("ConfigWindow.title")); //$NON-NLS-1$
    }

    public void open() {
        Rectangle parentBounds = getParent().getBounds();
        Point centerPoint = new Point();
        centerPoint.setLocation(parentBounds.getCenterX(), parentBounds.getCenterY());
        Dimension size = getSize();
        centerPoint.translate(-size.width / 2, -size.height / 2);
        setLocation(centerPoint);
        setVisible(true);
    }

}
