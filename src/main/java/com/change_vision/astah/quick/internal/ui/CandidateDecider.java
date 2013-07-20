package com.change_vision.astah.quick.internal.ui;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.Command;
import com.change_vision.astah.quick.command.annotations.Immediate;
import com.change_vision.astah.quick.command.candidates.InvalidState;
import com.change_vision.astah.quick.command.candidates.ValidState;
import com.change_vision.astah.quick.internal.command.CandidateHolder;
import com.change_vision.astah.quick.internal.command.CommandExecutor;
import com.change_vision.astah.quick.internal.ui.candidatesfield.CandidatesField;

public class CandidateDecider {

    private final QuickWindow quickWindow;
    private final CandidatesField candidatesField;
    private final CandidateHolder builder;
    private final CommandExecutor executor;

    public CandidateDecider(QuickWindow quickWindow, CandidatesField field, CandidateHolder builder) {
        this.quickWindow = quickWindow;
        this.candidatesField = field;
        this.builder = builder;
        this.executor = new CommandExecutor();
    }

    CandidateDecider(QuickWindow quickWindow, CandidatesField field, CandidateHolder builder, CommandExecutor executor) {
        this.quickWindow = quickWindow;
        this.candidatesField = field;
        this.builder = builder;
        this.executor = executor;
    }

    public void decide(Candidate candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException("candidate is null");
        }
        if (candidate instanceof InvalidState) {
            return;
        }
        if (candidate instanceof ValidState) {
            executeCommand(builder);
            return;
        }

        boolean decided = false;
        decided |= decideCandidate(builder, candidate);
        if (decided) return;
        decided |= decideCommand(builder, candidate);
        if (decided) return;
        throw new IllegalStateException("candidate is not command " + candidate);
    }

    private boolean decideCandidate(CandidateHolder builder, Candidate candidate) {
        if (builder.isCommitted()) {
            if (candidate instanceof Command) {
                throw new IllegalArgumentException("command is committed but candidate specify command. maybe it is bug.");
            }
            builder.add(candidate);
            String commandText = builder.getCommandText() + CommandExecutor.SEPARATE_COMMAND_CHAR;
            candidatesField.setText(commandText);
            if (isImmidiateCandidate(candidate)) {
                executeCommand(builder);
            }
            return true;
        }
        return false;
    }

    private boolean decideCommand(CandidateHolder builder, Candidate candidate) {
        if (candidate instanceof Command) {
            Command command = (Command) candidate;
            builder.commit(command);
            String commandText = builder.getCommandText() + CommandExecutor.SEPARATE_COMMAND_CHAR;
            candidatesField.setText(commandText);
            if (isImmidiateCommand(command)) {
                executeCommand(builder);
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

    private void executeCommand(CandidateHolder builder) {
        String candidateText = candidatesField.getText();
        quickWindow.close();
        try {
            executor.execute(builder, candidateText);
        } catch (Exception e) {
            quickWindow.notifyError("Alert", e.getMessage());
        }
        quickWindow.reset();
    }

}
