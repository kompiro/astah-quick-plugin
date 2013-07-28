package com.change_vision.astah.quick.internal.ui.candidatesfield;

import com.change_vision.astah.quick.internal.command.Candidates;
import com.change_vision.astah.quick.internal.command.CandidateHolder;
import com.change_vision.astah.quick.internal.ui.QuickWindow;
import com.change_vision.astah.quick.internal.ui.candidates.CandidatesListPanel;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidateWindowState;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidatesSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class CandidatesField extends JTextField {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CandidatesField.class);

    private final CandidatesListPanel candidatesList;

    private final QuickWindow quickWindow;

    private final Candidates candidates;

    private boolean settingText;

    private final CandidateHolder holder;

    public CandidatesField(QuickWindow quickWindow, CandidatesListPanel candidatesList, Candidates candidates, CandidatesSelector selector, CandidateHolder holder) {
        this.quickWindow = quickWindow;
        this.candidatesList = candidatesList;
        this.candidates = candidates;
        this.holder = holder;
        setFont(new Font("Dialog", Font.PLAIN, 32));
        setColumns(16);
        setEditable(true);
        if (candidatesList == null) {
            return;
        }
        CandidateAutoCompleteDocument document = new CandidateAutoCompleteDocument(this, candidates, holder);
//        document.setDocumentFilter(new CandidatesFieldDocumentFilter());
        CandidatesFieldDocumentListener listener = new CandidatesFieldDocumentListener(this,
                this.candidatesList, candidates, holder);
        document.addDocumentListener(listener);
        setDocument(document);
        CommitOrExecuteCommandAction commandAction = new CommitOrExecuteCommandAction(document, this.quickWindow, holder, selector);
        setAction(commandAction);
        new UpCandidatesListAction(this, this.candidatesList);
        new DownCandidatesListAction(this, this.candidatesList);


    }

//    public void setWindowState(CandidateWindowState windowState) {
//        switch (windowState) {
//            case Inputing:
//                openCandidatesList();
//                break;
//            case Wait:
//                closeCandidatesListAndReset();
//                break;
//            case ArgumentInputing:
//                openCandidatesList();
//                break;
//            case ArgumentWait:
//                closeCandidatesList();
//                break;
//            default:
//                throw new IllegalStateException("Illegal state of window: '" + windowState.name() + "'");
//        }
//    }

    @Override
    public void setText(String t) {
        settingText = true;
        super.setText(t);
        settingText = false;
    }

    public boolean isSettingText() {
        return settingText;
    }

//    private void openCandidatesList() {
//        if (candidatesList.isVisible() == false) {
//            logger.trace("openCandidatesList");
//            candidatesList.setVisible(true);
//        }
//        candidatesList.resetIndex();
//    }
//
//    private void closeCandidatesListAndReset() {
//        logger.trace("closeCandidatesListAndReset");
//        candidates.reset();
//    }
//
//    private void closeCandidatesList() {
//        logger.trace("closeCandidatesList");
//    }

}
