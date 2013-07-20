package com.change_vision.astah.quick.internal.ui.candidatesfield;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.internal.command.CandidateHolder;
import com.change_vision.astah.quick.internal.ui.CandidateDecider;
import com.change_vision.astah.quick.internal.ui.QuickWindow;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidatesSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
final class CommitOrExecuteCommandAction extends AbstractAction {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CommitOrExecuteCommandAction.class);

    private final CandidateDecider decider;

    private final CandidatesSelector selector;

    private final CandidateHolder builder;

    CommitOrExecuteCommandAction(CandidatesField field, QuickWindow quickWindow, CandidateHolder builder, CandidatesSelector selector) {
        super("commit-or-execute-command");
        this.decider = new CandidateDecider(quickWindow, field, builder);
        this.builder = builder;
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.trace("press enter");
        Candidate candidate = selector.current();
        decider.decide(candidate);
    }
}
