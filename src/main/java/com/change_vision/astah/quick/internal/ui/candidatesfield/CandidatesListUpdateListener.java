package com.change_vision.astah.quick.internal.ui.candidatesfield;

import com.change_vision.astah.quick.internal.ui.candidates.CandidatesListPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

final class CandidatesListUpdateListener implements DocumentListener {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CandidatesListUpdateListener.class);

    private final CandidatesListPanel candidatesList;

    public CandidatesListUpdateListener(CandidatesListPanel candidatesList) {
        this.candidatesList = candidatesList;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        logger.trace("insertUpdate");
        handleCandidatesList();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        logger.trace("changedUpdate");
        handleCandidatesList();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        logger.trace("removeUpdate");
        handleCandidatesList();
    }

    private void handleCandidatesList() {
        candidatesList.update();
    }

}
