package com.change_vision.astah.quick.internal.ui;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.Command;
import com.change_vision.astah.quick.command.annotations.Immediate;
import com.change_vision.astah.quick.command.candidates.InvalidState;
import com.change_vision.astah.quick.command.candidates.ValidState;
import com.change_vision.astah.quick.internal.command.CandidateHolder;
import com.change_vision.astah.quick.internal.command.CommandExecutor;
import com.change_vision.astah.quick.internal.ui.candidatesfield.CandidateAutoCompleteDocument;
import com.change_vision.astah.quick.internal.ui.candidatesfield.CandidatesField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

public class CandidateDecider {

    private final QuickWindow quickWindow;
    private final CandidateAutoCompleteDocument doc;
    private final CandidateHolder holder;
    private final CommandExecutor executor;

    private static final Logger logger = LoggerFactory.getLogger(CandidateDecider.class);

    public CandidateDecider(QuickWindow quickWindow, CandidateAutoCompleteDocument doc, CandidateHolder holder) {
        this.quickWindow = quickWindow;
        this.doc = doc;
        this.holder = holder;
        this.executor = new CommandExecutor();
    }

    CandidateDecider(QuickWindow quickWindow, CandidateAutoCompleteDocument doc, CandidateHolder holder, CommandExecutor executor) {
        this.quickWindow = quickWindow;
        this.doc = doc;
        this.holder = holder;
        this.executor = executor;
    }

    public void decide(Candidate candidate) {
        logger.trace("decide:{}",candidate);
        if (candidate == null) {
            throw new IllegalArgumentException("candidate is null");
        }
        if (candidate instanceof InvalidState) {
            return;
        }
        if (candidate instanceof ValidState) {
            executeCommand(holder);
            return;
        }

        boolean decided = false;
        decided |= decideCandidate(holder, candidate);
        if (decided) return;
        decided |= decideCommand(holder, candidate);
        if (decided) return;
        throw new IllegalStateException("candidate is not command " + candidate);
    }

    private boolean decideCandidate(CandidateHolder holder, Candidate candidate) {
        if (holder.isCommitted()) {
            if (candidate instanceof Command) {
                throw new IllegalArgumentException("command is committed but candidate specify command. maybe it is bug.");
            }
            holder.add(candidate);
            doc.commit();
            if (isImmidiateCandidate(candidate)) {
                executeCommand(holder);
            }
            return true;
        }
        return false;
    }

    private boolean decideCommand(CandidateHolder holder, Candidate candidate) {
        if (candidate instanceof Command) {
            Command command = (Command) candidate;
            holder.commit(command);
            doc.commit();
            if (isImmidiateCommand(command)) {
                executeCommand(holder);
            }
            return true;
        }
        return false;
    }

    private boolean isImmidiateCandidate(Candidate candidate) {
        return candidate.getClass().isAnnotationPresent(Immediate.class);
    }

    private boolean isImmidiateCommand(Command command) {
        return command.getClass().isAnnotationPresent(Immediate.class);
    }

    private void executeCommand(CandidateHolder holder) {
        quickWindow.close();
        try {
            executor.execute(holder, doc.getText(0,doc.getLength()));
        } catch (Exception e) {
            logger.warn("Exception occurred when command execution.",e);
            quickWindow.notifyError("Alert", e.getMessage());
        }
        quickWindow.reset();
    }

}
