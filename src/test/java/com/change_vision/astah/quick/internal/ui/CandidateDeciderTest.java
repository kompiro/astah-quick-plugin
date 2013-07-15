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
    private CandidatesSelector selector;

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
        decider = new CandidateDecider(window, candidatesField, executor);
        when(selector.getCandidatesObject()).thenReturn(candidates);
        when(candidates.getCommandBuilder()).thenReturn(builder);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decideWithNull(){
        decider.decide(null);
    }

   @Test(expected = IllegalStateException.class)
    public void decideWithCurrentSelectIsNull(){
       decider.decide(selector);
   }

    @Test
    public void decideWithInvalidState() throws ExecuteCommandException {
        when(selector.current()).thenReturn(invalidState);
        decider.decide(selector);

        verify(window,never()).close();
        verify(executor,never()).execute(any(CommandBuilder.class), anyString());
        verify(window,never()).notifyError(anyString(), anyString());
        verify(window,never()).reset();
    }

    @Test
    public void decideWithValidState() throws Exception {
        when(selector.current()).thenReturn(validState);
        decider.decide(selector);

        verify(window).close();
        verify(executor).execute(any(CommandBuilder.class), anyString());
        verify(window,never()).notifyError(anyString(), anyString());
        verify(window).reset();
    }

    @Test
    public void decideWithValidStateWhenThrowExceptionInExecute() throws Exception {
        when(selector.current()).thenReturn(validState);
        doThrow(new RuntimeException()).when(executor).execute(any(CommandBuilder.class), anyString());
        decider.decide(selector);

        verify(window).close();
        verify(window).notifyError(anyString(), anyString());
    }

    @Test(expected = IllegalStateException.class)
    public void decideCandidateWithCandidateWhenNotCommitted() throws Exception {
        when(selector.current()).thenReturn(candidate);
        decider.decide(selector);
    }

    @Test
    public void decideCandidateWithCandidate() throws Exception {
        when(builder.isCommitted()).thenReturn(true);
        when(selector.current()).thenReturn(candidate);
        decider.decide(selector);

        verify(builder).add(eq(candidate));
        verify(candidatesField).setText(anyString());

        verify(window,never()).close();
        verify(executor,never()).execute(eq(builder), anyString());
        verify(window,never()).reset();
    }

    @Test
    public void decideCandidateWithImmediateCandidate() throws Exception {
        when(builder.isCommitted()).thenReturn(true);
        when(selector.current()).thenReturn(immediateCandidate);
        decider.decide(selector);

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
        when(selector.current()).thenReturn(command);
        decider.decide(selector);
    }

    @Test
    public void decideCommandWithNotImmediateCommand() throws Exception {
        when(selector.current()).thenReturn(command);
        decider.decide(selector);

        verify(builder).commit(command);
        verify(candidatesField).setText(anyString());
    }

    @Test
    public void decideCommandWithImmediateCommand() throws Exception {
        when(selector.current()).thenReturn(immediateCommand);
        decider.decide(selector);

        verify(builder).commit(immediateCommand);
        verify(candidatesField).setText(anyString());

        verify(window).close();
        verify(executor).execute(eq(builder), anyString());
        verify(window,never()).notifyError(anyString(), anyString());
        verify(window).reset();

    }
}
