package com.change_vision.astah.quick.internal.ui.candidatesfield;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.Command;
import com.change_vision.astah.quick.internal.command.CandidateHolder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class CandidateTextParserTest {

    private static final String COMMAND_NAME = "create command";

    private static final String COMMAND_SEPARATOR = CandidateTextParser.SEPARATE_CANDIDATE_CHAR;

    private static final String CANDIDATE_ONE_NAME = "one";

    private static final String CANDIDATE_TWO_NAME = "two";

    private CandidateHolder holder;

    @Mock
    private Command command;

    @Mock
    private Candidate one;

    @Mock
    private Candidate two;

    private CandidateTextParser target;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.holder = new CandidateHolder();
        this.target = new CandidateTextParser(holder);
        when(this.command.getName()).thenReturn(COMMAND_NAME);
        when(this.one.getName()).thenReturn(CANDIDATE_ONE_NAME);
        when(this.two.getName()).thenReturn(CANDIDATE_TWO_NAME);
    }

    @Test
    public void getCandidateTextWhenUncommitted() throws Exception {
        String candidateText = target.getCandidateText("hoge");
        assertThat(candidateText, is("hoge"));
    }

    @Test
    public void candidateTextWhenCommitted() throws Exception {
        holder.commit(command);
        String candidateText = target.getCandidateText(COMMAND_NAME);
        assertThat(candidateText,is(""));
    }

    @Test
    public void candidateTextWhenCommittedAndSpace() throws Exception {
        holder.commit(command);
        String candidateText = target.getCandidateText(COMMAND_NAME + COMMAND_SEPARATOR);
        assertThat(candidateText,is(CandidateTextParser.SEPARATE_CANDIDATE_CHAR));
    }

    @Test
    public void candidateTextAfterCommitted() throws Exception {
        holder.commit(command);
        String candidateText = target.getCandidateText(COMMAND_NAME + COMMAND_SEPARATOR + "hoge");
        assertThat(candidateText,is("hoge"));
    }

    @Test
    public void candidateTextHasSpaceSequence() throws Exception {
        holder.commit(command);
        String candidateText = target.getCandidateText(COMMAND_NAME + COMMAND_SEPARATOR + COMMAND_SEPARATOR + "hoge");
        assertThat(candidateText,is("hoge"));
    }

    @Test
    public void getFullTextWhenEmpty() throws Exception {
        assertThat(target.getFullText(),is(""));
    }

    @Test
    public void getFullTextWithCommand() throws Exception {
        holder.commit(command);
        assertThat(target.getFullText(),is(COMMAND_NAME));
    }

    @Test
    public void getFullTextWithCommandAddCandidate() throws Exception {
        holder.commit(command);
        holder.add(one);
        assertThat(target.getFullText(),is(COMMAND_NAME + COMMAND_SEPARATOR + CANDIDATE_ONE_NAME));
    }

    @Test
    public void getFullTextWithCommandAddTwoCandidates() throws Exception {
        holder.commit(command);
        holder.add(one);
        holder.add(two);
        assertThat(target.getFullText(),is(COMMAND_NAME + COMMAND_SEPARATOR + CANDIDATE_ONE_NAME + COMMAND_SEPARATOR + CANDIDATE_TWO_NAME));
    }

}
