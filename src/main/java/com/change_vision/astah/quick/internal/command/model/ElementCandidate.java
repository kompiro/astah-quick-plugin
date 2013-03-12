package com.change_vision.astah.quick.internal.command.model;

import static java.lang.String.format;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.CandidateIconDescription;
import com.change_vision.astah.quick.internal.command.AstahCommandIconDescription;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.view.IconDescription;

class ElementCandidate implements Candidate {
    
    private INamedElement element;

    ElementCandidate(INamedElement element) {
        this.element = element;
    }

    @Override
    public String getName() {
        return element.getName();
    }

    @Override
    public String getDescription() {
        String fullName = element.getFullName(".");
        return format("%s", fullName);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public CandidateIconDescription getIconDescription() {
        return new AstahCommandIconDescription(IconDescription.MODEL);
    }
    
    INamedElement getElement() {
        return element;
    }

}
