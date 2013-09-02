package com.change_vision.astah.quick.internal.ui.candidatesfield;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.candidates.NotFound;
import com.change_vision.astah.quick.command.candidates.ValidState;
import com.change_vision.astah.quick.internal.annotations.TestForMethod;
import com.change_vision.astah.quick.internal.command.CandidateHolder;
import com.change_vision.astah.quick.internal.command.Candidates;
import com.change_vision.astah.quick.internal.ui.candidates.CandidatesListPanel;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidatesSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 */
public class CandidateAutoCompleteDocument extends PlainDocument {
    private final CandidatesField documentOwner;
    private final Candidates candidates;
    private final CandidateTextParser parser;
    private final CandidateHolder holder;

    private final Logger logger = LoggerFactory.getLogger(CandidateAutoCompleteDocument.class);
    private final CandidatesListPanel candidatesList;

    public CandidateAutoCompleteDocument(CandidatesField documentOwner,CandidatesListPanel candidatesList, Candidates candidates, CandidateHolder holder) {
        this.documentOwner = documentOwner;
        this.candidatesList = candidatesList;
        this.candidates = candidates;
        this.holder = holder;
        this.parser = new CandidateTextParser(holder);
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        logger.trace("start insertString: offs:'{}' str:'{}' attr:'{}'",new Object[]{offs,str,a});
        if (str == null || str.length() == 0) {
            return;
        }

        String text = getText(0, offs);
        String completion = complete(text + str);
        logger.trace("completion: '{}'", completion);
        int length = offs + str.length();
        logger.trace("length: '{}'", length);
        if (completion != null && text.length() + str.length() > 0) {
            if (holder.isCommitted()){
                StringBuilder builder = new StringBuilder(holder.getCommandText());
                builder.append(CandidateHolder.SEPARATE_COMMAND_CHAR);
                String base = builder.toString();
                int substringStart = length - base.length() - 1;
                substringStart = substringStart < 0 ? 0 : substringStart;
                logger.trace("substring start: '{}",substringStart);
                str = completion.substring(substringStart);
            } else {
                str = completion.substring(length - 1);

            }
            logger.trace("do insertString: offs:'{}' str:'{}' attr:'{}'",new Object[]{offs,str,a});
            super.insertString(offs, str, a);
            if (length <= getLength()){
                documentOwner.select(length, getLength());
            }
        } else {
            logger.trace("do insertString: offs:'{}' str:'{}' attr:'{}'",new Object[]{offs,str,a});
            super.insertString(offs, str, a);
        }
    }

    @Override
    public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        logger.trace("start replace offset:'{}' length:'{}' text:'{}' attrs:'{}'",
                new Object[]{offset, length, text, attrs});
        super.replace(offset, length, text, attrs);
    }

    @Override
    protected void postRemoveUpdate(DefaultDocumentEvent chng) {
        logger.trace("postRemoveUpdate");
        String commandText = holder.getCommandText();
        String text = getText(this);
        logger.trace("removed text'{}'", text);
        if (text.isEmpty() == false && commandText.length() > text.length()) {
            holder.removeCandidate();
        }
        if (text.isEmpty()) {
            holder.reset();
        }
        String candidateText = parser.getCandidateText(text);
        candidates.filter(candidateText);
        candidatesList.update();
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        logger.trace("remove offs:'{}' len:'{}'",offs,len);
        super.remove(offs, len);
    }

    public void commit() {
        StringBuilder builder = new StringBuilder(holder.getCommandText());
        builder.append(CandidateHolder.SEPARATE_COMMAND_CHAR);
        String commandString = builder.toString();
        replaceString(commandString);
        setCaretToLast();
        candidatesList.update();
    }

    private void setCaretToLast() {
        documentOwner.setCaretPosition(getLength());
    }

    @Override
    protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
        if (isInputConversion(attr)){
            return;
        }
        super.insertUpdate(chng, attr);
        candidatesList.update();
    }

    private boolean isInputConversion(AttributeSet attrSet) {
        return attrSet != null
                && attrSet.isDefined(StyleConstants.ComposedTextAttribute);
    }

    private String complete(String str) {
        String candidateText = parser.getCandidateText(str);
        candidates.filter(candidateText);
        if (candidates.getCandidates() == null ||
            candidates.getCandidates().length == 0) {
            return candidateText;
        }
        Candidate topCandidate = candidates.getCandidates()[0];
        if (topCandidate instanceof ValidState ||
            topCandidate instanceof NotFound){
            return candidateText;
        }
        return topCandidate.getName();
    }

    private String getText(Document document){
        int length = document.getLength();
        try {
            return document.getText(0,length);
        } catch (BadLocationException e) {
            logger.warn("Bad Location",e);
            return "";
        }
    }

    void updateCandidate(Candidate candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException("candidate is null.");
        }
        StringBuilder builder = new StringBuilder(holder.getCommandText());
        logger.trace("updateCandidate committed:'{}'", holder.isCommitted());
        if (holder.isCommitted()){
            builder.append(CandidateHolder.SEPARATE_COMMAND_CHAR);
        }

        int committedPosition = builder.length();

        String name = candidate.getName();
        builder.append(name);
        String commandString = builder.toString();
        replaceString(commandString);
        logger.trace("command:'{}'",commandString);
        logger.trace("select position:'{}' length:'{}'", committedPosition, getLength());
        documentOwner.select(committedPosition,getLength());
    }

    private void replaceString(String str) {
        Content content = getContent();
        writeLock();
        try {
            content.remove(0, getLength());
            content.insertString(0, str);
        } catch (BadLocationException e) {
            logger.warn("Bad Location",e);
        }finally {
            writeUnlock();
        }
    }

    @TestForMethod
    String getText() throws BadLocationException {
        return getText(0, getLength());
    }

}
