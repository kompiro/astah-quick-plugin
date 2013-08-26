package com.change_vision.astah.quick.internal.ui.candidates;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.internal.command.Candidates;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidatesSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseListener;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

@SuppressWarnings("serial")
public class CandidatesListPanel extends JPanel {

    private final static class CandidateSelectionListener implements ListSelectionListener {
        private final CandidatesList candidateList;
        private final CandidatesSelector selector;

        private CandidateSelectionListener(CandidatesList candidateList, CandidatesSelector selector) {
            this.candidateList = candidateList;
            this.selector = selector;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int index = candidateList.getSelectedIndex();
            selector.setCurrentIndex(index);
        }
    }

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CandidatesListPanel.class);

    private final Candidates candidates;
    private final CandidatesList candidateList;
    private final CandidatesSelector selector;

    private JScrollPane scrollPane;

    public CandidatesListPanel(Candidates candidates, CandidatesSelector selector) {
        this.candidateList = new CandidatesList();
        this.selector = selector;
        this.candidates = candidates;
        scrollPane = new JScrollPane(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setAutoscrolls(true);
        candidateList.addListSelectionListener(new CandidateSelectionListener(candidateList, selector));
        scrollPane.setViewportView(candidateList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(0, 0, getBackground().brighter().brighter(), 0,
                getHeight(), getBackground());

        graphics.setPaint(gp);
        graphics.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(graphics);
    }

    public void update() {
        selector.setCurrentIndex(0);
        Candidate[] candidatesData = candidates.getCandidates();

        candidateList.setListData(candidatesData);
        if (candidatesData.length > 0) {
            resetCandidateListIndex();
        }
    }

    private void resetCandidateListIndex() {
        candidateList.setSelectedIndex(0);
        scrollPane.getViewport().setViewPosition(new Point(0, 0));
    }

    public void up() {
        selector.up();
        Candidate command = selector.current();
        logger.trace("up : current '{}'", command);
        candidateList.setSelectedValue(command, true);
    }

    public void down() {
        selector.down();
        Candidate candidate = selector.current();
        logger.trace("down : current '{}'", candidate);
        candidateList.setSelectedValue(candidate, true);
    }

    @Override
    public void setVisible(boolean visible) {
        logger.trace("visible:{}", visible);
        super.setVisible(visible);
    }

    public void addMouseListener(MouseListener listener) {
        candidateList.addMouseListener(listener);
    }

}
