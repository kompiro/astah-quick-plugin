package com.change_vision.astah.quick.internal.ui.candidatesfield;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.Command;
import com.change_vision.astah.quick.internal.command.CandidateHolder;

/**
 * parse CandidatesField's text
 */
public class CandidateTextParser {

    static final String SEPARATE_CANDIDATE_CHAR = CandidateHolder.SEPARATE_COMMAND_CHAR;
    private final CandidateHolder holder;

    public CandidateTextParser(CandidateHolder holder) {
        this.holder = holder;
    }

    public String getCandidateText(String text){
        if (holder.isUncommited()) {
            return text;
        }
        Command command = holder.getCommand();
        String commandName = command.getName();
        String[] commandWords = commandName.split(SEPARATE_CANDIDATE_CHAR);
        String[] candidateWords = text.split(SEPARATE_CANDIDATE_CHAR);
        if (candidateWords.length > commandWords.length) {
            StringBuilder builder = new StringBuilder();
            for(int i = commandWords.length; i < candidateWords.length; i++){
                builder.append(candidateWords[i]);
                builder.append(SEPARATE_CANDIDATE_CHAR);
            }
            return builder.toString().trim();
        }
        if (text.length() > commandName.length()) {
            return text.substring(commandName.length());
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * get
     * @return command and candidates text
     */
    public String getFullText(){
        if (holder.isUncommited()) {
            return ""; //$NON-NLS-1$
        }
        Command command = holder.getCommand();
        StringBuilder textBuilder = new StringBuilder(command.getName());
        Candidate[] candidates = holder.getCandidates();
        for (Candidate candidate : candidates) {
            textBuilder.append(SEPARATE_CANDIDATE_CHAR);
            textBuilder.append(candidate.getName());
        }
        return textBuilder.toString();
    }
}
