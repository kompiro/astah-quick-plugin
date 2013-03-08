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
import com.change_vision.astah.quick.internal.command.CommandExecutor;

public class CandidatesSelectorTest {
	
	private CandidatesSelector<Candidate> selector;
	
	@Mock
	private Candidate one;
	
	@Mock
	private Candidate two;

    @Mock
    private CommandExecutor executor;

	@Before
	public void before() throws Exception {
		MockitoAnnotations.initMocks(this);
		selector = new CandidatesSelector<Candidate>(executor);
		
		when(one.getName()).thenReturn("one");
		when(one.getName()).thenReturn("two");
	}

	@Test
	public void initialState() {
		selector.up();
		selector.down();
		Candidate current = selector.current();
		assertThat(current,is(instanceOf(NotFound.class)));
	}
	
	@Test
	public void onlyOneCandidate() throws Exception {
		selector.setCandidates(new Candidate[]{
			one	
		});
		selector.up();
		selector.down();
		Candidate current = selector.current();
		assertThat(current,is(one));
	}
	
	@Test
	public void twoCandidates() throws Exception {
		selector.setCandidates(new Candidate[]{
				one,
				two
			});
		selector.down();
		Candidate current = selector.current();
		assertThat(current,is(two));

		selector.up();
		current = selector.current();
		assertThat(current,is(one));

		selector.up();
		current = selector.current();
		assertThat(current,is(two));
		selector.down();
		current = selector.current();
		assertThat(current,is(one));
		
	}

}
