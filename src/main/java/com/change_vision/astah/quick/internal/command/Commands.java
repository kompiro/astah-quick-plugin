package com.change_vision.astah.quick.internal.command;

import java.util.ArrayList;
import java.util.List;

import org.osgi.util.tracker.ServiceTracker;

import com.change_vision.astah.quick.command.Command;
import com.change_vision.astah.quick.internal.annotations.TestForMethod;
import com.change_vision.astah.quick.internal.command.diagram.DiagramCommands;
import com.change_vision.astah.quick.internal.command.environment.EnvironmentCommands;
import com.change_vision.astah.quick.internal.command.model.ModelCommands;
import com.change_vision.astah.quick.internal.command.project.ProjectCommands;

/**
 * The set of Command for Astah Quick Plugin.<BR/>
 *
 * This class uses ServiceTracker to add the command registered by other plugin.<br/>
 * It provides to add extension point for Astah Quick Plugin.
 */
public class Commands {

    private final List<Command> definedCommands = new ArrayList<Command>();
    private ServiceTracker tracker;
    
    public Commands(ServiceTracker tracker){
        this.tracker = tracker;
        initCommands();
    }

    public void initCommands() {
        definedCommands.addAll(ModelCommands.commands());
        definedCommands.addAll(DiagramCommands.commands());
        definedCommands.addAll(ProjectCommands.commands());
        definedCommands.addAll(EnvironmentCommands.commands());
    }
    
    @TestForMethod
    public void add(Command command) {
        definedCommands.add(command);
    }

    @TestForMethod
    public void clear() {
        definedCommands.clear();
    }
    
    public List<Command> getCommands() {
        List<Command> allCommands = new ArrayList<Command>();
        allCommands.addAll(definedCommands);
        Object[] services = tracker.getServices();
        if (services == null) return allCommands;
        for (Object object : services) {
            if (object instanceof Command) {
                Command command = (Command) object;
                allCommands.add(command);
            }
        }
        return allCommands;
    }

}
