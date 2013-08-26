package com.change_vision.astah.quick.internal.ui.candidatesfield;

import com.change_vision.astah.quick.internal.command.Candidates;
import com.change_vision.astah.quick.internal.command.CandidateHolder;
import com.change_vision.astah.quick.internal.ui.QuickWindow;
import com.change_vision.astah.quick.internal.ui.candidates.CandidatesListPanel;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidatesSelector;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@SuppressWarnings("serial")
public class CandidatesField extends JTextField {

    public CandidatesField(QuickWindow quickWindow, CandidatesListPanel candidatesList, Candidates candidates, CandidatesSelector selector, CandidateHolder holder) {
        setFont(new Font("Dialog", Font.PLAIN, 32));
        setColumns(16);
        setEditable(true);

        CandidateAutoCompleteDocument document = new CandidateAutoCompleteDocument(this, candidatesList, candidates, holder);
        setDocument(document);
        PropertyChangeListener selectionChangeListener = new CandidateSelectionChangeListener(document);
        selector.addPropertyChangeListener(selectionChangeListener);

        CommitOrExecuteCommandAction commandAction = new CommitOrExecuteCommandAction(document, quickWindow, holder, selector);
        setAction(commandAction);
        new UpCandidatesListAction(this, candidatesList);
        new DownCandidatesListAction(this, candidatesList);
    }

}
