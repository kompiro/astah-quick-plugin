package com.change_vision.astah.quick.internal.ui.candidatesfield.state;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.candidates.NotFound;
import com.change_vision.astah.quick.internal.command.Candidates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class CandidatesSelector {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CandidatesSelector.class);

    public static final String PROP_OF_CANDIDATE = "candidate";

    private int currentIndex;
    private final Candidates candidates;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

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
        fireIndexChange(oldValue, currentIndex);
    }

    public Candidate current() {
        if (getCandidates().length == 0) {
            return new NotFound();
        }
        return getCandidates()[currentIndex];
    }

    public void down() {
        int oldIndex = currentIndex;
        currentIndex++;
        if (currentIndex >= getCandidates().length) {
            currentIndex = 0;
        }
        fireIndexChange(oldIndex, currentIndex);
    }

    public Candidate[] getCandidates() {
        Candidate[] result = this.candidates.getCandidates();
        if (result == null) {
            return new Candidate[0];
        }
        return result;
    }

    public void setCurrentIndex(int index) {
        int oldValue = currentIndex;
        currentIndex = index;
        fireIndexChange(oldValue, currentIndex);
    }

    private void fireIndexChange(int oldValue, int newValue) {
        logger.trace("{}: old:'{}' new:'{}'", new Object[]{"currentIndex", oldValue, newValue});
        if (oldValue > -1 && newValue > -1 && getCandidates() != null && getCandidates().length != 0) {
            support.firePropertyChange(PROP_OF_CANDIDATE,getCandidates()[oldValue],getCandidates()[newValue]);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
