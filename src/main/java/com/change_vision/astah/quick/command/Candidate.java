package com.change_vision.astah.quick.command;

public interface Candidate {
	
	public abstract String getName();
	
	public abstract String getDescription();
	
	public abstract boolean isEnable();
	
	public abstract CandidateIconDescription getIconDescription();

}
