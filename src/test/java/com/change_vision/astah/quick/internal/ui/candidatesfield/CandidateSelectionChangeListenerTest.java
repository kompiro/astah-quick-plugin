package com.change_vision.astah.quick.internal.ui.candidatesfield;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.candidates.NotFound;
import com.change_vision.astah.quick.command.candidates.ValidState;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidatesSelector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.beans.PropertyChangeEvent;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CandidateSelectionChangeListenerTest {

    @Mock
    private CandidateAutoCompleteDocument document;

    @Mock
    private PropertyChangeEvent event;

    private CandidateSelectionChangeListener testTarget;

    @Before
    public void before(){
        testTarget = new CandidateSelectionChangeListener(document);
    }

    @Test(expected = IllegalArgumentException.class)
    public void propertyChangedWithNull(){
        testTarget.propertyChange(null);
    }

    @Test(expected = IllegalStateException.class)
    public void propertyChangedWithNullCandidateEvent(){
        when(event.getPropertyName()).thenReturn(CandidatesSelector.PROP_OF_CANDIDATE);
        testTarget.propertyChange(event);
    }

    @Test(expected = IllegalStateException.class)
    public void propertyChangeWithTheEventDoesNotHaveACandidateInstance() {
        when(event.getPropertyName()).thenReturn(CandidatesSelector.PROP_OF_CANDIDATE);
        when(event.getNewValue()).thenReturn(new Object());

        testTarget.propertyChange(event);
    }

    @Test
    public void propertyChangeWithCandidateEvent(){
        Candidate candidate = mock(Candidate.class);
        when(event.getPropertyName()).thenReturn(CandidatesSelector.PROP_OF_CANDIDATE);
        when(event.getNewValue()).thenReturn(candidate);

        testTarget.propertyChange(event);

        verify(document).updateCandidate(eq(candidate));
    }

    @Test
    public void propertyChangeWithValidStateCandidateEvent() {
        Candidate candidate = mock(ValidState.class);
        when(event.getPropertyName()).thenReturn(CandidatesSelector.PROP_OF_CANDIDATE);
        when(event.getNewValue()).thenReturn(candidate);

        testTarget.propertyChange(event);

        verify(document,never()).updateCandidate(eq(candidate));
    }

    @Test
    public void propertyChangeWithNotFoundCandidateEvent() {
        Candidate candidate = mock(NotFound.class);
        when(event.getPropertyName()).thenReturn(CandidatesSelector.PROP_OF_CANDIDATE);
        when(event.getNewValue()).thenReturn(candidate);

        testTarget.propertyChange(event);

        verify(document,never()).updateCandidate(eq(candidate));

    }
}
