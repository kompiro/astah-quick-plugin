package com.change_vision.astah.quick.internal.ui.candidatesfield;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.candidates.NotFound;
import com.change_vision.astah.quick.command.candidates.ValidState;
import com.change_vision.astah.quick.internal.command.CandidateHolder;
import com.change_vision.astah.quick.internal.command.Candidates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.*;

/**
 *
 */
public class CandidateAutoCompleteDocument extends PlainDocument{
    private final CandidatesField documentOwner;
    private final Candidates candidates;
    private final CandidateTextParser parser;
    private final CandidateHolder holder;

    private final Logger logger = LoggerFactory.getLogger(CandidateAutoCompleteDocument.class);

    public CandidateAutoCompleteDocument(CandidatesField documentOwner, Candidates candidates, CandidateHolder holder) {
        logger.trace("created");
        this.documentOwner = documentOwner;
        this.candidates = candidates;
        this.holder = holder;
        this.parser = new CandidateTextParser(holder);
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        logger.trace("start insertString: '{}'",str);
        if (str == null || str.length() == 0) {
            return;
        }

        String text = getText(0, offs); // Current text.
        String completion = complete(text + str);
        logger.trace("completion: '{}'", completion);
        int length = offs + str.length();
        logger.trace("length: '{}'", length);
        if (completion != null && text.length() + str.length() > 0) {
            if (holder.isCommitted()){
                StringBuilder builder = new StringBuilder();
                builder.append(holder.getCommandText());
                builder.append(CandidateHolder.SEPARATE_COMMAND_CHAR);
                String base = builder.toString();
                int substringStart = length - base.length() - 1;
                substringStart = substringStart < 0 ? 0 : substringStart;
                logger.trace("substring start: '{}",substringStart);
                str = completion.substring(substringStart);
            } else {
                str = completion.substring(length - 1);

            }
            super.insertString(offs, str, a);
            documentOwner.select(length, getLength());
        } else {
            super.insertString(offs, str, a);
        }
    }

    @Override
    protected void postRemoveUpdate(DefaultDocumentEvent chng) {
        logger.trace("postRemoveUpdate");
        String commandText = holder.getCommandText();
        String text = getText(this);
        if (text.isEmpty() == false && commandText.length() > text.length()) {
            holder.removeCandidate();
        }
        if (text.isEmpty()) {
            holder.reset();
        }
        String candidateText = parser.getCandidateText(text);
        candidates.filter(candidateText);
    }

    public void commit() {
        StringBuilder builder = new StringBuilder();
        builder.append(holder.getCommandText());
        builder.append(CandidateHolder.SEPARATE_COMMAND_CHAR);
        String base = builder.toString();
        String insert = base.substring(getLength());
        try {
            super.insertString(getLength(), insert, new SimpleAttributeSet());
        } catch (BadLocationException e) {
        }
        documentOwner.setCaretPosition(getLength());
    }

    @Override
    protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
        if (isInputConversion(attr)){
            return;
        }
        super.insertUpdate(chng, attr);
    }

    private boolean isInputConversion(AttributeSet attrSet) {
        return attrSet != null
                && attrSet.isDefined(StyleConstants.ComposedTextAttribute);
    }

    private String complete(String str) {
        String candidateText = parser.getCandidateText(str);
        candidates.filter(candidateText);
        Candidate topCandidate = candidates.getCandidates()[0];
        if (topCandidate instanceof ValidState ||
            topCandidate instanceof NotFound ||
            topCandidate == null){
            return candidateText;
        }
        return topCandidate.getName();
    }

    private String getText(Document document){
        try {
            return document.getText(0,document.getLength());
        } catch (BadLocationException e) {
            return "";
        }
    }

}
