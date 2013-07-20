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

    private final CandidateHolder builder;

    public CandidatesFieldDocumentListener(
            CandidatesField candidatesField,
            CandidatesListPanel candidatesList,
            Candidates candidates,
            CandidateHolder builder) {
        this.field = candidatesField;
        this.candidatesList = candidatesList;
        this.candidates = candidates;
        this.builder = builder;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        logger.trace("insertUpdate");
        handleCandidatesList(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        logger.trace("changedUpdate");
        handleCandidatesList(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        logger.trace("removeUpdate");
        Document document = e.getDocument();
        String commandText = builder.getCommandText();
        String text = getText(document);
        if (text.isEmpty() == false && commandText.length() > text.length()) {
            builder.removeCandidate();
        }
        if (text.isEmpty() && field.isSettingText() == false) {
            builder.reset();
        }
        String candidateText = builder.getCandidateText(text);
        candidatesList.setCandidateText(candidateText);
    }

    private void handleCandidatesList(DocumentEvent event) {
        Document document = event.getDocument();
        String text =  getText(document);
        String candidateText = builder.getCandidateText(text);
        candidatesList.setCandidateText(candidateText);
        if (isNullOrEmpty(candidateText)) {
            if (isNullOrEmpty(text)) {
                field.setWindowState(CandidateWindowState.ArgumentWait);
            } else {
                field.setWindowState(CandidateWindowState.Wait);
            }
        } else {
            if (isNullOrEmpty(text)) {
                field.setWindowState(CandidateWindowState.ArgumentInputing);
            } else {
                field.setWindowState(CandidateWindowState.Inputing);
            }
        }
    }

    private boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    private String getText(Document document){
        try {
            return document.getText(0,document.getLength());
        } catch (BadLocationException e) {
            return "";
        }
    }

}
