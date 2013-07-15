package com.change_vision.astah.quick.internal.ui;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.Command;
import com.change_vision.astah.quick.command.annotations.Immediate;
import com.change_vision.astah.quick.command.candidates.InvalidState;
import com.change_vision.astah.quick.command.candidates.ValidState;
import com.change_vision.astah.quick.command.exception.ExecuteCommandException;
import com.change_vision.astah.quick.internal.command.Candidates;
import com.change_vision.astah.quick.internal.command.CommandBuilder;
import com.change_vision.astah.quick.internal.command.CommandExecutor;
import com.change_vision.astah.quick.internal.ui.candidatesfield.CandidatesField;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidatesSelector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

public class CandidateDeciderTest {

    @Immediate
    private static abstract class ImmediateCommand implements Command {

    }

    @Immediate
    private static abstract class ImmediateCandidate implements Candidate {

    }

    @Mock
    private QuickWindow window;

    @Mock
    private CandidatesField candidatesField;

    @Mock
    private Candidates candidates;

    @Mock
    private CommandBuilder builder;

    @Mock
    private CommandExecutor executor;

    @Mock
    private InvalidState invalidState;

    @Mock
    private ValidState validState;

    @Mock
    private Command command;

    @Mock
    private ImmediateCommand immediateCommand;

    @Mock
    private Candidate candidate;

    @Mock
    private ImmediateCandidate immediateCandidate;

    private CandidateDecider decider;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        decider = new CandidateDecider(window, candidatesField,builder , executor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decideWithNull(){
        decider.decide(null);
    }

   @Test(expected = IllegalArgumentException.class)
    public void decideWithCurrentSelectIsNull(){
       decider.decide(null);
   }

    @Test
    public void decideWithInvalidState() throws ExecuteCommandException {
        decider.decide(invalidState);

        verify(window,never()).close();
        verify(executor,never()).execute(any(CommandBuilder.class), anyString());
        verify(window,never()).notifyError(anyString(), anyString());
        verify(window,never()).reset();
    }

    @Test
    public void decideWithValidState() throws Exception {
        decider.decide(validState);

        verify(window).close();
        verify(executor).execute(any(CommandBuilder.class), anyString());
        verify(window,never()).notifyError(anyString(), anyString());
        verify(window).reset();
    }

    @Test
    public void decideWithValidStateWhenThrowExceptionInExecute() throws Exception {
        doThrow(new RuntimeException()).when(executor).execute(any(CommandBuilder.class), anyString());
        decider.decide(validState);

        verify(window).close();
        verify(window).notifyError(anyString(), anyString());
    }

    @Test(expected = IllegalStateException.class)
    public void decideCandidateWithCandidateWhenNotCommitted() throws Exception {
        decider.decide(candidate);
    }

    @Test
    public void decideCandidateWithCandidate() throws Exception {
        when(builder.isCommitted()).thenReturn(true);
        decider.decide(candidate);

        verify(builder).add(eq(candidate));
        verify(candidatesField).setText(anyString());

        verify(window,never()).close();
        verify(executor,never()).execute(eq(builder), anyString());
        verify(window,never()).reset();
    }

    @Test
    public void decideCandidateWithImmediateCandidate() throws Exception {
        when(builder.isCommitted()).thenReturn(true);
        decider.decide(immediateCandidate);

        verify(builder).add(eq(immediateCandidate));
        verify(candidatesField).setText(anyString());

        verify(window).close();
        verify(executor).execute(eq(builder), anyString());
        verify(window,never()).notifyError(anyString(), anyString());
        verify(window).reset();

    }

    @Test(expected = IllegalArgumentException.class)
    public void decideCandidateWithCommand() throws Exception {
        when(builder.isCommitted()).thenReturn(true);
        decider.decide(command);
    }

    @Test
    public void decideCommandWithNotImmediateCommand() throws Exception {
        decider.decide(command);

        verify(builder).commit(command);
        verify(candidatesField).setText(anyString());
    }

    @Test
    public void decideCommandWithImmediateCommand() throws Exception {
        decider.decide(immediateCommand);

        verify(builder).commit(immediateCommand);
        verify(candidatesField).setText(anyString());

        verify(window).close();
        verify(executor).execute(eq(builder), anyString());
        verify(window,never()).notifyError(anyString(), anyString());
        verify(window).reset();

    }
}
