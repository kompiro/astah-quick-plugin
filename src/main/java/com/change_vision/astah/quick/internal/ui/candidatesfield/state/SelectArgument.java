package com.change_vision.astah.quick.internal.ui.candidatesfield.state;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.CandidatesProvider;
import com.change_vision.astah.quick.command.Command;
import com.change_vision.astah.quick.command.candidates.NotFound;
import com.change_vision.astah.quick.command.candidates.ValidState;
import com.change_vision.astah.quick.internal.command.CandidateHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectArgument implements CandidateState {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SelectArgument.class);

    private CandidateHolder candidateHolder;

    public SelectArgument(CandidateHolder candidateHolder) {
        this.candidateHolder = candidateHolder;
    }

    @Override
    public Candidate[] filter(String key) {
        logger.trace("candidates:{}", key);
        Candidate[] candidates;
        Command committed = candidateHolder.getCommand();
        if (committed instanceof CandidatesProvider) {
            CandidatesProvider provider = (CandidatesProvider) committed;
            Candidate[] committedCandidates = candidateHolder.getCandidates();
            candidates = provider.candidate(committedCandidates, key);
            if (candidates == null) {
                return new Candidate[]{
                        new NotFound()
                };
            }
            return candidates;
        } else {
            return new Candidate[]{
                    new ValidState(committed, key)
            };
        }
    }

}
