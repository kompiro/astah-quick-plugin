package com.change_vision.astah.quick.internal.ui.candidatesfield;

import com.change_vision.astah.quick.internal.command.CandidateHolder;
import com.change_vision.astah.quick.internal.command.Candidates;
import com.change_vision.astah.quick.internal.ui.candidates.CandidatesListPanel;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidateWindowState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

final class CandidatesFieldDocumentListener implements DocumentListener {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CandidatesFieldDocumentListener.class);

    private final CandidatesField field;

    private final CandidatesListPanel candidatesList;

    private final Candidates candidates;

    private final CandidateHolder holder;

    private final CandidateTextParser parser;

    public CandidatesFieldDocumentListener(
            CandidatesField candidatesField,
            CandidatesListPanel candidatesList,
            Candidates candidates,
            CandidateHolder holder) {
        this.field = candidatesField;
        this.candidatesList = candidatesList;
        this.candidates = candidates;
        this.holder = holder;
        this.parser = new CandidateTextParser(holder);
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
