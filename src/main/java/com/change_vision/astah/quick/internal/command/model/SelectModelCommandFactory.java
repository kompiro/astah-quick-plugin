package com.change_vision.astah.quick.internal.command.model;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.Command;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;

public class SelectModelCommandFactory {

    private ModelAPI api = new ModelAPI();

    public List<Candidate> create(String key) {
        INamedElement[] foundModels = api.find(key);
        if (foundModels == null || foundModels.length == 0) {
            return new ArrayList<Candidate>();
        }
        List<Candidate> candidates = new ArrayList<Candidate>();
        for (INamedElement foundModel : foundModels) {
            Command selectCommand = createSelectCommand(foundModel);
            candidates.add(selectCommand);
        }
        return candidates;
    }

    public void setApi(ModelAPI api) {
        this.api = api;
    }

    private Command createSelectCommand(INamedElement foundModel) {
        if (foundModel instanceof IClass) {
            IClass classModel = (IClass) foundModel;
            boolean isInterface = classModel.hasStereotype("interface");
            if (isInterface) {
                return new SelectInterfaceCommand(classModel);
            }
            boolean isActor = classModel.hasStereotype("actor");
            if (isActor) {
                return new SelectActorCommand(classModel);
            }
            return new SelectClassCommand(classModel);
        }
        if (foundModel instanceof IPackage) {
            IPackage packageModel = (IPackage) foundModel;
            return new SelectPackageCommand(packageModel);
        }
        return new SelectModelCommand(foundModel);
    }

}