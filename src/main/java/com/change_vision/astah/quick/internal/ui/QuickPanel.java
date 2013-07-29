package com.change_vision.astah.quick.internal.ui;

import com.change_vision.astah.quick.command.Candidate;
import com.change_vision.astah.quick.command.CandidateIconDescription;
import com.change_vision.astah.quick.command.Command;
import com.change_vision.astah.quick.internal.command.CandidateHolder;
import com.change_vision.astah.quick.internal.command.Candidates;
import com.change_vision.astah.quick.internal.ui.candidates.CandidatesListPanel;
import com.change_vision.astah.quick.internal.ui.candidatesfield.CandidateAutoCompleteDocument;
import com.change_vision.astah.quick.internal.ui.candidatesfield.CandidatesField;
import com.change_vision.astah.quick.internal.ui.candidatesfield.state.CandidatesSelector;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;

@SuppressWarnings("serial")
public class QuickPanel extends JPanel implements PropertyChangeListener {

    private final Candidates candidates;

    private static final class CandidateDoubleClickListener extends MouseAdapter {
        private final CandidateDecider decider;
        private final CandidatesSelector selector;

        private CandidateDoubleClickListener(QuickWindow quickWindow, CandidateAutoCompleteDocument document,
                                             CandidateHolder builder, CandidatesSelector selector) {
            this.decider = new CandidateDecider(quickWindow, document, builder);
            this.selector = selector;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {
                Candidate candidate = selector.current();
                decider.decide(candidate);
            }
        }
    }

    private final CandidatesField candidatesField;
//    private HelpField helpField;
    private final JLabel iconLabel;
    private final Icon astahIcon;
    private final CandidatesListPanel candidatesList;

    public QuickPanel(final QuickWindow quickWindow, Candidates candidates, CandidateHolder builder) {
        this.candidates = candidates;
        setLayout(new MigLayout("", "[32px][grow]", "[][][]"));
        CandidatesSelector selector = new CandidatesSelector(candidates);
        candidatesList = new CandidatesListPanel(candidates, selector);

        URL astahIconURL = this.getClass().getResource("/icons/astah_icon_professional.png");
        BufferedImage image;
        try {
            image = ImageIO.read(astahIconURL);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        astahIcon = new ImageIcon(image.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        iconLabel = new JLabel(astahIcon);
        builder.addPropertyChangeListener(this);
        add(iconLabel, "cell 0 0,left");
        candidatesField = new CandidatesField(quickWindow, candidatesList, candidates, selector, builder);
        add(candidatesField, "cell 1 0,growx");
//        helpField = new HelpField();
//        add(helpField, "cell 1 1,growx");
        add(candidatesList, "cell 0 2,span 2,growx");
        CandidateAutoCompleteDocument document = (CandidateAutoCompleteDocument) candidatesField.getDocument();
        candidatesList.addMouseListener(new CandidateDoubleClickListener(quickWindow, document, builder, selector));
    }

    public void opened() {
        candidates.filter("");
        candidatesList.update();
    }

    public void reset() {
        iconLabel.setIcon(astahIcon);
        candidatesField.setText("");
        candidatesList.update();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(CandidateHolder.PROP_OF_COMMAND)) {
            Object newValue = evt.getNewValue();
            if (newValue instanceof Command) {
                Command command = (Command) newValue;
                updateIcon(command);
                return;
            }
            iconLabel.setIcon(astahIcon);
        }
    }

    private void updateIcon(Command command) {
        CandidateIconDescription iconDescription = command.getIconDescription();
        Icon icon = iconDescription.getIcon();
        BufferedImage bufferedImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        icon.paintIcon(null, graphics, 8, 8);
        graphics.dispose();
        iconLabel.setIcon(new ImageIcon(bufferedImage));
    }

//    public CandidatesListPanel getCandidatesList() {
//        return this.candidatesList;
//    }

}
