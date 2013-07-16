package com.change_vision.astah.quick.internal.command;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.Command;
import com.change_vision.astah.quick.internal.annotations.TestForMethod;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidateState;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.SelectArgument;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.SelectCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Comparator;

import static java.lang.String.format;

public class Candidates implements PropertyChangeListener {

    private final static class CandidateNameComparator implements Comparator<Candidate> {
        @Override
        public int compare(Candidate o1, Candidate o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    static class SelectCommandFactory {
        private Commands commands;

        SelectCommandFactory(Commands commands) {
            this.commands = commands;
        }

        SelectCommand create() {
            return new SelectCommand(commands);
        }
    }

    public static final String PROP_STATE = "state"; //$NON-NLS-1$

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(Candidates.class);

    private Commands commands;

    private SelectCommandFactory commandFactory;

    private CandidateState state;

    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    private final CommandBuilder commandBuilder;

    private Candidate[] candidates = new Candidate[0];

    public Candidates(Commands commands, CommandBuilder commandBuilder) {
        this.commands = commands;
        this.commandBuilder = commandBuilder;
        this.commandBuilder.addPropertyChangeListener(this);
        this.commandFactory = new SelectCommandFactory(commands);
        this.state = commandFactory.create();
    }

    public void filter(String key) {
        if (key == null) throw new IllegalArgumentException("key is null."); //$NON-NLS-1$
        String searchKey = key.trim();
        logger.trace("key:'{}'", searchKey); //$NON-NLS-1$
        if (isChangedToCommandState(searchKey)) {
            SelectCommand newState = commandFactory.create();
            setState(newState);
        }
        this.candidates = doFilter(searchKey);
        if (isChangedArgumentState(key, candidates)) {
            logger.trace("change argument state");
            Command committed = (Command) candidates[0];
            if (commandBuilder.isCommitted() == false) {
                commandBuilder.commit(committed);
            }
            // re-filtering
            this.candidates = doFilter(searchKey);
            return;
        }
        if (isCommitCandidate(key, candidates)) {
            commandBuilder.add(candidates[0]);
        }
    }

    private Candidate[] doFilter(String searchKey) {
        Candidate[] candidates = state.filter(searchKey);
        if (candidates == null) {
            illegalStateOfCandidatesAreNull();
        }
        logger.trace("state:'{}' candidates:'{}'", state.getClass().getSimpleName(), candidates); //$NON-NLS-1$
        return candidates;
    }

    private void illegalStateOfCandidatesAreNull() {
        String className = state.getClass().getSimpleName();
        if (state instanceof SelectArgument) {
            className = commandBuilder.getCommand().getClass().getSimpleName();
        }
        String message = format("state returns null candidates. %s", className);
        throw new IllegalStateException(message);
    }

    private boolean isCommitCandidate(String key, Candidate[] candidates) {
        boolean isCurrentArgumentState = state instanceof SelectArgument;
        boolean isFoundOnlyOneCandidate = candidates.length == 1 && candidates[0] instanceof Candidate;
        if (isCurrentArgumentState == false || isFoundOnlyOneCandidate == false) {
            return false;
        }
        Candidate candidate = candidates[0];
        String candidateName = candidate.getName().toLowerCase();
        String keyLowerCase = key.toLowerCase().trim();
        boolean isDecidedByKey = keyLowerCase.equals(candidateName);
        return isCurrentArgumentState && isFoundOnlyOneCandidate && isDecidedByKey;
    }

    private boolean isChangedArgumentState(String key, Candidate[] candidates) {
        boolean isCurrentCommandState = state instanceof SelectCommand;
        boolean isFoundOnlyOneCommand = candidates.length == 1 && candidates[0] instanceof Command;
        if (isCurrentCommandState == false || isFoundOnlyOneCommand == false) {
            return false;
        }
        Command committed = (Command) candidates[0];
        String commandName = committed.getName().toLowerCase();
        boolean isCommittedByKey = key.startsWith(commandName);
        return isCurrentCommandState && isFoundOnlyOneCommand && (isCommittedByKey || commandBuilder.isCommitted());
    }

    private boolean isChangedToCommandState(String key) {
        return (state instanceof SelectArgument) && commandBuilder.isCommitted() == false;
    }

    public void setState(CandidateState newState) {
        CandidateState oldState = this.state;
        this.state = newState;
        support.firePropertyChange(PROP_STATE, oldState, newState);
    }

    public CandidateState getState() {
        return state;
    }

    public Candidate[] getCandidates() {
        if (isCommitted()) {
            Arrays.sort(this.candidates, new CandidateNameComparator());
        }
        return this.candidates;
    }

    public boolean isCommitted() {
        return commandBuilder.isCommitted();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @TestForMethod
    void setCommandFactory(SelectCommandFactory commandFactory) {
        this.commandFactory = commandFactory;
        this.state = commandFactory.create();
    }

    public void reset() {
        this.state = commandFactory.create();
        this.commandBuilder.reset();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName.equals(CommandBuilder.PROP_OF_COMMAND)) {
            SelectArgument newState = new SelectArgument(commandBuilder);
            setState(newState);
        }
    }

}
