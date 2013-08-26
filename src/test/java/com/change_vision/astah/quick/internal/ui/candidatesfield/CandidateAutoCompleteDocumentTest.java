package com.change_vision.astah.quick.internal.ui.candidatesfield;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.Command;
import com.change_vision.astah.quick.command.candidates.NotFound;
import com.change_vision.astah.quick.internal.command.CandidateHolder;
import com.change_vision.astah.quick.internal.command.Candidates;
import com.change_vision.astah.quick.internal.ui.candidates.CandidatesListPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import java.beans.PropertyChangeEvent;

@RunWith(MockitoJUnitRunner.class)
public class CandidateAutoCompleteDocumentTest {

    @Mock
    private CandidatesField documentOwner;

    @Mock
    private CandidatesListPanel candidatesList;

    @Mock
    private Candidates candidates;

    @Mock
    private Command foundCommand;

    @Mock
    private Candidate firstCandidate;

    @Mock
    private Candidate foundCandidate;

    private CandidateHolder holder;

    private CandidateAutoCompleteDocument document;

    @Mock
    private PropertyChangeEvent event;

    @Before
    public void before() {
        holder = new CandidateHolder();
        document = new CandidateAutoCompleteDocument(documentOwner,candidatesList,candidates,holder);
    }

    @Test
    public void insertStringWithSimpleStringWhenUncommittedAndFoundNoCandidates() throws BadLocationException{
        when(candidates.getCandidates()).thenReturn(new Candidate[0]);

        document.insertString(0, "a", null);

        verify(documentOwner).select(1,1);
        String actual = document.getText();
        assertThat(actual,is("a"));
    }

    @Test
    public void insertStringWithSimpleStringWhenUncommittedAndNotFound() throws BadLocationException {
        Candidate[] found = new Candidate[]{
                new NotFound()
        };
        when(candidates.getCandidates()).thenReturn(found);

        document.insertString(0, "a", null);

        verify(documentOwner).select(1, 1);
        String actual = document.getText();
        assertThat(actual, is("a"));
    }

    @Test
    public void insertStringWithSimpleStringWhenUncommittedAndFound() throws BadLocationException {
        when(foundCommand.getName()).thenReturn("Association");
        Candidate[] found = new Candidate[]{
                foundCommand
        };
        when(candidates.getCandidates()).thenReturn(found);

        document.insertString(0, "a", new SimpleAttributeSet());

        verify(documentOwner).select(1,11);
        String actual = document.getText();
        assertThat(actual,is("Association"));
    }

    @Test
    public void insertTwoCharsWhenUncommittedAndFound() throws BadLocationException {
        when(foundCommand.getName()).thenReturn("Association");
        Candidate[] found = new Candidate[]{
                foundCommand
        };
        when(candidates.getCandidates()).thenReturn(found);

        document.insertString(0, "a", null);
        document.replace(1, 10, "s", null);

        String actual = document.getText();
        assertThat(actual, is("Association"));
        verify(documentOwner).select(1, 11);
        verify(documentOwner).select(2, 11);
    }

    @Test
    public void deleteCharWhenUncommittedAndFound() throws BadLocationException {
        when(foundCommand.getName()).thenReturn("Association");
        Candidate[] found = new Candidate[]{
                foundCommand
        };
        when(candidates.getCandidates()).thenReturn(found);

        document.insertString(0,"a", null);
        // push delete
        document.remove(1,10);

        String actual = document.getText();
        assertThat(actual, is("A"));
    }

    @Test
    public void deleteCharsWhenUncommittedAndFound() throws BadLocationException {
        when(foundCommand.getName()).thenReturn("Association");
        Candidate[] found = new Candidate[]{
                foundCommand
        };
        when(candidates.getCandidates()).thenReturn(found);

        document.insertString(0,"a", null);
        document.replace(1, 10, "s", null);

        // push delete
        document.remove(2,9);

        String actual = document.getText();
        assertThat(actual, is("As"));
    }

    @Test
    public void commitWhenUncommittedAndNotCommandText() throws BadLocationException {
        document.commit();

        String actual = document.getText();
        assertThat(actual, is(" "));
        verify(candidatesList).update();
    }

    @Test
    public void commitWhenCommitted() throws BadLocationException {
        when(foundCommand.getName()).thenReturn("Association");
        holder.commit(foundCommand);

        document.commit();

        String actual = document.getText();
        assertThat(actual,is("Association "));
        verify(candidatesList).update();
        verify(documentOwner).setCaretPosition(12);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateCandidateWithNull() throws BadLocationException {
        document.updateCandidate(null);
    }

    @Test
    public void updateCandidateWithFoundCommand() throws BadLocationException {
        when(foundCommand.getName()).thenReturn("Association");
        document.updateCandidate(foundCommand);

        String actual = document.getText();
        assertThat(actual,is("Association"));
    }

    @Test
    public void updateCandidateWithFoundSecondCandidate() throws BadLocationException {
        when(foundCommand.getName()).thenReturn("create class");
        holder.commit(foundCommand);
        when(foundCandidate.getName()).thenReturn("Association");

        document.insertString(0,"create class",null);
        document.updateCandidate(foundCandidate);

        String actual = document.getText();
        assertThat(actual,is("create class Association"));
    }

    @Test
    public void updateCandidateWithFoundThirdCandidate() throws BadLocationException {
        when(foundCommand.getName()).thenReturn("add stereotype");
        holder.commit(foundCommand);

        when(firstCandidate.getName()).thenReturn("InterfaceA");
        holder.add(firstCandidate);
        when(foundCandidate.getName()).thenReturn("interface");

        document.insertString(0,"add stereotype InterfaceA",null);
        document.updateCandidate(foundCandidate);

        String actual = document.getText();
        assertThat(actual,is("add stereotype InterfaceA interface"));
    }
}
