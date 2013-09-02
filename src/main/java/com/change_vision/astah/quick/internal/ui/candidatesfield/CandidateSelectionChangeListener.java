package com.change_vision.astah.quick.internal.ui.candidatesfield;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.candidates.NotFound;
import com.change_vision.astah.quick.command.candidates.ValidState;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidatesSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class CandidateSelectionChangeListener implements PropertyChangeListener {

    private final Logger logger = LoggerFactory.getLogger(CandidateSelectionChangeListener.class);

    private final CandidateAutoCompleteDocument document;

    public CandidateSelectionChangeListener(CandidateAutoCompleteDocument document) {
        this.document = document;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        logger.trace("property change : {}",evt);
        if (evt == null) throw new IllegalArgumentException();
        String propertyName = evt.getPropertyName();
        if ( propertyName != null && propertyName.equals(CandidatesSelector.PROP_OF_CANDIDATE)){
            logger.trace("prop candidate changed: {}",evt.getNewValue());
            Candidate candidate = getNewCandidate(evt);
            if(isUpdateNeeded(candidate)) {
                this.document.updateCandidate(candidate);
                return;
            }
        }
    }

    private boolean isUpdateNeeded(Candidate candidate) {
        return !(candidate instanceof ValidState) && !(candidate instanceof NotFound);
    }

    private Candidate getNewCandidate(PropertyChangeEvent evt) {
        Object newValue = evt.getNewValue();
        if (newValue == null || Candidate.class.isInstance(newValue) == false) {
            throw new IllegalStateException("event candidate is null.");
        }
        return (Candidate) newValue;
    }
}
