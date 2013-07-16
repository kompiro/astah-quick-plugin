package com.change_vision.astah.quick.internal.ui.configure;

import com.change_vision.astah.quick.internal.model.QuickProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
class SaveAction extends AbstractAction {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SaveAction.class);

    final private ConfigPanel configPanel;

    SaveAction(ConfigPanel configPanel) {
        super("save");
        this.configPanel = configPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String keyStroke = configPanel.getKeyStroke();
        logger.trace("save:{}", keyStroke);
        QuickProperties properties = configPanel.getProperties();
        properties.setKeyStroke(keyStroke);
        properties.store();
        JDialog window = configPanel.getWindow();
        window.setVisible(false);
        window.dispose();
    }

}
