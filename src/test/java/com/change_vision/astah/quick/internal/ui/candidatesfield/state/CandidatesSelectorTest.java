package com.change_vision.astah.quick.internal.ui.candidatesfield.state;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.candidates.NotFound;

public class CandidatesSelectorTest {

    private CandidatesSelector<Candidate> selector;

    @Mock
    private Candidate one;

    @Mock
    private Candidate two;

    @Mock
    private Candidate three;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        selector = new CandidatesSelector<Candidate>();

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
        selector.setCandidates(new Candidate[] { one });
        selector.up();
        selector.down();
        Candidate current = selector.current();
        assertThat(current, is(one));
    }

    @Test
    public void twoCandidates() throws Exception {
        selector.setCandidates(new Candidate[] { one, two });
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
        selector.setCandidates(new Candidate[] { one, two, three });

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

}
