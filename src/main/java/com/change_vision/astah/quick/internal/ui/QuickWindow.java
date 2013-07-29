package com.change_vision.astah.quick.internal.ui;

import com.change_vision.astah.quick.internal.command.CandidateHolder;
import com.change_vision.astah.quick.internal.command.Candidates;
import com.change_vision.astah.quick.internal.command.Commands;

import javax.swing.*;
import java.awt.Dialog.ModalExclusionType;
import java.awt.*;
import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class QuickWindow extends JWindow {

    private final QuickPanel quickPanel;
    private final MessageNotifier notifier;

    public QuickWindow(JFrame parent, Commands commands) {
        super(parent);
        this.notifier = new MessageNotifier(parent);
        final CandidateHolder builder = new CandidateHolder();
        InputMap inputMap = getRootPane()
                .getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close-it");
        CloseAction closeAction = new CloseAction(this);
        getRootPane().getActionMap().put("close-it", closeAction);
        Candidates candidates = new Candidates(commands, builder);
        this.quickPanel = new QuickPanel(this, candidates, builder);
        getContentPane().add(quickPanel);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        pack();
        DragMove.install(this);
    }

    public void notifyError(String title, String message) {
        notifier.notifyError(title, message);
    }

    public void close() {
        setVisible(false);
    }

    public void reset() {
        quickPanel.reset();
    }

    public void open() {
        Rectangle parentBounds = getParent().getBounds();
        Point centerPoint = new Point();
        centerPoint.setLocation(parentBounds.getCenterX(), parentBounds.getCenterY());
        Dimension size = getSize();
        centerPoint.translate(-size.width / 2, -size.height / 2);
        setLocation(centerPoint);
        setVisible(true);
        quickPanel.opened();
    }

}
