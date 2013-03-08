package com.change_vision.astah.quick.internal.ui.candidatesfield.state;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.Command;
import com.change_vision.astah.quick.internal.annotations.TestForMethod;
import com.change_vision.astah.quick.internal.command.diagram.DiagramCommands;
import com.change_vision.astah.quick.internal.command.model.ModelCommands;
import com.change_vision.astah.quick.internal.command.model.SelectModelCommandFactory;
import com.change_vision.astah.quick.internal.command.project.ProjectCommands;

public class SelectCommand implements CandidateState {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SelectCommand.class);

    private final List<Command> allCommands = new ArrayList<Command>();

    static {
    }

    private SelectModelCommandFactory commandFactory = new SelectModelCommandFactory();

    public SelectCommand() {
        initCommands();
    }

    @Override
    public Candidate[] filter(String key) {
        logger.trace("key:{}", key);
        List<Candidate> candidates = new ArrayList<Candidate>();
        if (key == null || key.isEmpty()) {
            for (Command command : allCommands) {
                if (command.isEnabled()) {
                    candidates.add(command);
                }
            }
            return candidates.toArray(new Candidate[] {});
        }
        for (Command command : allCommands) {
            String commandName = command.getName();
            if (command.isEnabled() && isCandidate(key, commandName)) {
                candidates.add(command);
            }
        }
        logger.trace("command candidates:{}", candidates);
        List<Candidate> selectCommands = commandFactory.create(key);
        candidates.addAll(selectCommands);

        if (candidates.size() == 0) {
            candidates.add(new NotFound());
        }
        return candidates.toArray(new Candidate[] {});
    }

    private boolean isCandidate(String searchKey, String commandName) {
        return commandName.startsWith(searchKey);
    }

    @TestForMethod
    public void add(Command command) {
        allCommands.add(command);
    }

    @TestForMethod
    public void clear() {
        allCommands.clear();
    }

    @TestForMethod
    public void initCommands() {
        allCommands.addAll(ModelCommands.commands());
        allCommands.addAll(ProjectCommands.commands());
        allCommands.addAll(DiagramCommands.commands());
    }

    @TestForMethod
    void setCommandFactory(SelectModelCommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

}
