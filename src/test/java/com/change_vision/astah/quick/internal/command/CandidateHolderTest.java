package com.change_vision.astah.quick.internal.command;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.Command;

public class CandidateHolderTest {
    
    private static final String COMMAND_NAME = "create command";
    
    private static final String COMMAND_SEPARATOR = " ";

    private static final String CANDIDATE_ONE_NAME = "one";

    private static final String CANDIDATE_TWO_NAME = "two";
    
    @Mock
    private Command command;
    
    @Mock
    private Candidate one;

    @Mock
    private Candidate two;

    private CandidateHolder holder;

    @Mock
    private PropertyChangeListener propertyChangeListener;
    
    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        holder = new CandidateHolder();
        when(command.getName()).thenReturn(COMMAND_NAME);
        when(one.getName()).thenReturn(CANDIDATE_ONE_NAME);
        when(two.getName()).thenReturn(CANDIDATE_TWO_NAME);

    }
    
    @Test
    public void init() throws Exception {
        Command actual = holder.getCommand();
        assertThat(actual, is(nullValue()));
        assertThat(holder.isCommitted(),is(false));
        Candidate[] candidates = holder.getCandidates();
        assertThat(candidates,is(notNullValue()));
        assertThat(candidates.length,is(0));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void commitWithNull() throws Exception {
        holder.commit(null);
    }

    @Test
    public void commitAndGetCommand() {
        holder.commit(command);
        Command actual = holder.getCommand();
        assertThat(actual, is(notNullValue()));
        assertThat(holder.isCommitted(),is(true));
        assertThat(holder.getCommandText(),is(COMMAND_NAME));

    }
    
    @Test
    public void receivePropertyChangeByCommit() throws Exception {
        holder.addPropertyChangeListener(propertyChangeListener);
        holder.commit(command);
        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(propertyChangeListener).propertyChange(captor.capture());
        PropertyChangeEvent event = captor.getValue();
        assertThat(event.getPropertyName(),is(CandidateHolder.PROP_OF_COMMAND));
    }
    
    @Test
    public void addCandidate() throws Exception {
        holder.commit(command);
        holder.add(one);
        Candidate[] candidates = holder.getCandidates();
        assertThat(candidates, is(notNullValue()));
        assertThat(candidates.length,is(1));
        assertThat(holder.getCommandText(),is(COMMAND_NAME + COMMAND_SEPARATOR + CANDIDATE_ONE_NAME));

        holder.add(two);
        candidates = holder.getCandidates();
        assertThat(candidates, is(notNullValue()));
        assertThat(candidates.length,is(2));
        assertThat(holder.getCommandText(), is(COMMAND_NAME + COMMAND_SEPARATOR + CANDIDATE_ONE_NAME + COMMAND_SEPARATOR + CANDIDATE_TWO_NAME));
    }

    @Test
    public void receivePropertyChangeByAdd() throws Exception {
        holder.addPropertyChangeListener(propertyChangeListener);
        holder.add(one);
        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(propertyChangeListener).propertyChange(captor.capture());
        PropertyChangeEvent event = captor.getValue();
        assertThat(event.getPropertyName(),is(CandidateHolder.PROP_OF_CANDIDATE));
    }
    
    @Test
    public void removeACandidate() throws Exception {
        holder.add(one);
        holder.remove(one);
        Candidate[] candidates = holder.getCandidates();
        assertThat(candidates,is(notNullValue()));
        assertThat(candidates.length,is(0));
    }
    
    @Test
    public void removeCandidateNotContained() throws Exception {
        boolean remove = holder.remove(one);
        assertThat(remove,is(false));
    }
    
    @Test
    public void receivePropertyChangeByRemove() throws Exception {
        holder.addPropertyChangeListener(propertyChangeListener);
        holder.add(one);
        holder.remove(one);
        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(propertyChangeListener,times(2)).propertyChange(captor.capture());
        PropertyChangeEvent event = captor.getValue();
        assertThat(event.getPropertyName(),is(CandidateHolder.PROP_OF_CANDIDATE));
    }
    
    @Test
    public void removeCandidate() throws Exception {
        holder.commit(command);
        holder.add(one);
        holder.add(two);
        holder.removeCandidate();
        assertThat(holder.getCommand(),is(notNullValue()));
        Candidate[] candidates = holder.getCandidates();
        assertThat(candidates,is(notNullValue()));
        assertThat(candidates.length, is(1));
        assertThat(candidates[0],is(one));
        holder.removeCandidate();
        candidates = holder.getCandidates();
        assertThat(holder.getCommand(), is(notNullValue()));
        assertThat(candidates,is(notNullValue()));
        assertThat(candidates.length, is(0));
        holder.removeCandidate();
        assertThat(holder.getCommand(),is(nullValue()));
    }
    
    @Test
    public void removeCandidate_candidateFirePropertyChangeEvent() throws Exception {
        holder.commit(command);
        holder.add(one);
        holder.addPropertyChangeListener(propertyChangeListener); // start to receive event
        holder.removeCandidate();
        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(propertyChangeListener).propertyChange(captor.capture());
        PropertyChangeEvent event = captor.getValue();
        assertThat(event.getPropertyName(),is(CandidateHolder.PROP_OF_CANDIDATE));
    }

    @Test
    public void removeCandidate_commandFirePropertyChangeEvent() throws Exception {
        holder.commit(command);

        holder.addPropertyChangeListener(propertyChangeListener); // start to receive event
        holder.removeCandidate();
        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(propertyChangeListener).propertyChange(captor.capture());
        PropertyChangeEvent event = captor.getValue();
        assertThat(event.getPropertyName(),is(CandidateHolder.PROP_OF_COMMAND));
    }

}
