package com.change_vision.astah.quick.internal.ui.candidatesfield.state;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

import com.change_vision.astah.quick.internal.command.Candidates;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.candidates.NotFound;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CandidatesSelectorTest {

    private CandidatesSelector selector;

    @Mock
    private Candidate one;

    @Mock
    private Candidate two;

    @Mock
    private Candidate three;

    @Mock
    private Candidates candidates;

    @Mock
    private PropertyChangeListener listener;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        selector = new CandidatesSelector(candidates);

        when(one.getName()).thenReturn("one");
        when(two.getName()).thenReturn("two");
        when(three.getName()).thenReturn("three");
    }

    @Test
    public void initialState() {
        selector.up();
        selector.down();
        Candidate current = selector.current();
        assertThat(current, is(instanceOf(NotFound.class)));
    }

    @Test
    public void onlyOneCandidate() throws Exception {
        when(candidates.getCandidates()).thenReturn(new Candidate[]{one});
        selector.up();
        selector.down();
        Candidate current = selector.current();
        assertThat(current, is(one));
    }

    @Test
    public void twoCandidates() throws Exception {
        when(candidates.getCandidates()).thenReturn(new Candidate[] { one, two });
        selector.down();
        Candidate current = selector.current();
        assertThat(current, is(two));

        selector.up();
        current = selector.current();
        assertThat(current, is(one));

        selector.up();
        current = selector.current();
        assertThat(current, is(two));
        selector.down();
        current = selector.current();
        assertThat(current, is(one));

    }
    
    @Test
    public void setSelectionIndex() throws Exception {
        when(candidates.getCandidates()).thenReturn(new Candidate[] { one, two, three });

        selector.setCurrentIndex(1);

        Candidate current = selector.current();
        assertThat(current, is(two));

        selector.setCurrentIndex(2);
        current = selector.current();
        assertThat(current, is(three));

        selector.setCurrentIndex(0);
        current = selector.current();
        assertThat(current, is(one));
        
        selector.setCurrentIndex(2);
        current = selector.current();
        assertThat(current, is(three));
    }

    @Test
    public void notNotifyWhenCandidatesIsZero() {
        selector.addPropertyChangeListener(listener);
        selector.down();

        verify(listener,never()).propertyChange((PropertyChangeEvent)anyObject());

        selector.up();

        verify(listener,never()).propertyChange((PropertyChangeEvent)anyObject());
    }

    @Test
    public void notifyChangedWhenSetCurrentIndex(){
        when(candidates.getCandidates()).thenReturn(new Candidate[] { one, two, three });
        selector.addPropertyChangeListener(listener);
        selector.setCurrentIndex(2);

        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(captor.capture());
        PropertyChangeEvent event = captor.getValue();

        assertThat(event.getPropertyName(),is(CandidatesSelector.PROP_OF_CANDIDATE));
        assertThat((Candidate)event.getOldValue(),is(one));
        assertThat((Candidate)event.getNewValue(),is(three));
    }

    @Test
    public void notifyChangedWhenDown(){
        when(candidates.getCandidates()).thenReturn(new Candidate[] { one, two, three });
        selector.addPropertyChangeListener(listener);
        selector.down();

        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(captor.capture());
        PropertyChangeEvent event = captor.getValue();

        assertThat(event.getPropertyName(),is(CandidatesSelector.PROP_OF_CANDIDATE));
        assertThat((Candidate)event.getOldValue(),is(one));
        assertThat((Candidate)event.getNewValue(),is(two));
    }

    @Test
    public void notifyChangedWhenUp(){
        when(candidates.getCandidates()).thenReturn(new Candidate[] { one, two, three });
        selector.addPropertyChangeListener(listener);
        selector.up();

        ArgumentCaptor<PropertyChangeEvent> captor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(listener).propertyChange(captor.capture());
        PropertyChangeEvent event = captor.getValue();

        assertThat(event.getPropertyName(),is(CandidatesSelector.PROP_OF_CANDIDATE));
        assertThat((Candidate)event.getOldValue(),is(one));
        assertThat((Candidate)event.getNewValue(),is(three));
    }

    @Test
    public void stopToNotifyChangedWhenRemoveListener() {
        when(candidates.getCandidates()).thenReturn(new Candidate[] { one, two, three });
        selector.addPropertyChangeListener(listener);
        selector.up();
        selector.removePropertyChangeListener(listener);
        selector.down();

        verify(listener,atLeastOnce()).propertyChange((PropertyChangeEvent)anyObject());

    }

}
