package com.change_vision.astah.quick.internal.ui.candidatesfield;

import java.awt.Font;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.quick.internal.command.Candidates;
import com.change_vision.astah.quick.internal.command.CommandExecutor;
import com.change_vision.astah.quick.internal.ui.QuickWindow;
import com.change_vision.astah.quick.internal.ui.candidates.CandidatesListWindow;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidateWindowState;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.SelectCommand;

@SuppressWarnings("serial")
public final class CandidatesField extends JTextField implements PropertyChangeListener {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CandidatesField.class);

    private final CandidatesListWindow candidatesList;

    private final QuickWindow quickWindow;

    private boolean settingText;

    public CandidatesField(QuickWindow quickWindow, CandidatesListWindow candidatesList) {
        this.quickWindow = quickWindow;
        this.candidatesList = candidatesList;
        setFont(new Font("Dialog", Font.PLAIN, 32));
        setColumns(16);
        setEditable(true);
        candidatesList.getCandidates().addPropertyChangeListener(this);
        new CommitOrExecuteCommandAction(this, this.quickWindow, this.candidatesList);
        new CommitCommandAction(this, this.candidatesList);
        new UpCandidatesListAction(this, this.candidatesList);
        new DownCandidatesListAction(this, this.candidatesList);

        CandidatesFieldDocumentListener listener = new CandidatesFieldDocumentListener(this,
                this.candidatesList);
        
        getDocument().addDocumentListener(listener);
    }

    public void setWindowState(CandidateWindowState windowState) {
        switch (windowState) {
        case Inputing:
            openCandidatesList();
            break;
        case Wait:
            closeCandidatesListAndReset();
            break;
        case ArgumentInputing:
            openCandidatesList();
            break;
        case ArgumentWait:
            closeCandidatesList();
            break;
        default:
            throw new IllegalStateException("Illegal state of window: '" + windowState.name() + "'");
        }
    }
    
    @Override
    public void setText(String t) {
        settingText = true;
        super.setText(t);
        settingText = false;
    }
    
    public boolean isSettingText() {
        return settingText;
    }

    private void openCandidatesList() {
        if (candidatesList.isVisible() == false) {
            logger.trace("openCandidatesList");
            Point location = (Point) quickWindow.getLocation().clone();
            location.translate(0, quickWindow.getSize().height);
            candidatesList.setLocation(location);
            candidatesList.setAlwaysOnTop(true);
            candidatesList.open();
        }
    }

    private void closeCandidatesListAndReset() {
        logger.trace("closeCandidatesListAndReset");
        Candidates commands = candidatesList.getCandidates();
        commands.setState(new SelectCommand());
        candidatesList.close();
        CommandExecutor executor = quickWindow.getExecutor();
        executor.reset();
    }

    private void closeCandidatesList() {
        logger.trace("closeCandidatesList");
        candidatesList.close();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Candidates.PROP_STATE)) {
            evt.getOldValue();
        }
    }

    public String getCandidateText() {
        CommandExecutor executor = quickWindow.getExecutor();
        return executor.getCandidateText(getText());
    }

    public CommandExecutor getExecutor() {
        return quickWindow.getExecutor();
    }

}
