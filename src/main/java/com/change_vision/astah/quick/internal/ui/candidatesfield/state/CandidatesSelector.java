package com.change_vision.astah.quick.internal.ui.candidatesfield.state;

import com.change_vision.astah.quick.internal.command.Candidates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.candidates.NotFound;

public class CandidatesSelector {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CandidatesSelector.class);

    private int currentIndex;
    private Candidates candidates;

    public CandidatesSelector(Candidates candidates) {
        this.candidates = candidates;
    }

    public void up() {
        if (getCandidates().length == 0) return;
        int oldValue = currentIndex;
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = getCandidates().length - 1;
        }
        firePropertyChange("currentIndex", oldValue, currentIndex);
    }

    public Candidate current() {
        if (getCandidates().length == 0) {
            return new NotFound();
        }
        return getCandidates()[currentIndex];
    }

    public void down() {
        int oldValue = currentIndex;
        currentIndex++;
        if (currentIndex >= getCandidates().length) {
            currentIndex = 0;
        }
        firePropertyChange("currentIndex", oldValue, currentIndex);
    }

    public void firePropertyChange(String propertyName, Object oldValue,
                                   Object newValue) {
        logger.trace("{}: old:'{}' new:'{}'", new Object[]{propertyName, oldValue, newValue});
    }

    public Candidate[] getCandidates() {
        Candidate[] result = this.candidates.getCandidates();
        if(result == null){
            return new Candidate[0];
        }
        return result;
    }

    public Candidates getCandidatesObject() {
        return this.candidates;
    }

    public void setCurrentIndex(int index) {
        int oldValue = currentIndex;
        currentIndex = index;
        firePropertyChange("currentIndex", oldValue, currentIndex);
    }
}
