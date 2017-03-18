package uk.co.nickthecoder.wrkfoo.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class FindToolBar extends JPanel implements SearcherListener
{
    private Searcher searcher;

    private JTextField textField;

    private JButton prevButton;

    private JButton nextButton;

    private JLabel label;

    private JCheckBox matchWholeWord;

    private JCheckBox matchRegex;

    private JCheckBox matchCase;

    public JPanel rightPanel;

    public FindToolBar(Searcher s)
    {
        this.searcher = s;
        this.setLayout(new BorderLayout());

        JPanel squashed = new JPanel();
        squashed.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); // new BoxLayout(squashed, BoxLayout.X_AXIS));
        squashed.add(new JLabel("Find : "));
        this.add(squashed, BorderLayout.WEST);

        textField = new JTextField(15);
        squashed.add(textField);

        textField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                searcher.setSearchText(textField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                searcher.setSearchText(textField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
            }

        });
        // TODO Listen for ENTER key

        ActionBuilder builder = new ActionBuilder(searcher).component(this);
        prevButton = builder.name("findPrev").buildButton();
        nextButton = builder.name("findNext").buildButton();

        builder = new ActionBuilder(this);

        squashed.add(prevButton);
        squashed.add(nextButton);

        label = new JLabel("");
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        this.add(label);

        rightPanel = new JPanel();
        this.add(rightPanel, BorderLayout.EAST);

        matchCase = builder.name("matchCase").label("Match Case").buildCheckBox();
        rightPanel.add(matchCase);

        matchWholeWord = builder.name("matchWholeWord").label("Whole Whole Word").buildCheckBox();
        rightPanel.add(matchWholeWord);

        matchRegex = builder.name("matchRegex").label("Regex").buildCheckBox();
        rightPanel.add(matchRegex);

        searcher.addSearcherListener(this);
    }

    public void onMatchCase()
    {
        searcher.context.setMatchCase(matchCase.isSelected());
        searcher.performSearch();
    }

    public void onMatchWholeWord()
    {
        searcher.context.setWholeWord(matchWholeWord.isSelected());
        searcher.performSearch();
    }

    public void onMatchRegex()
    {
        searcher.context.setRegularExpression(matchRegex.isSelected());
        searcher.performSearch();
    }

    public void setVisible(boolean show)
    {
        super.setVisible(show);
        if (show) {
            matchRegex.setSelected(searcher.context.isRegularExpression());
            matchWholeWord.setSelected(searcher.context.getWholeWord());
            textField.selectAll();
            textField.requestFocus();
            searcher.setSearchText(textField.getText());
        }
    }

    @Override
    public void searched(SearcherEvent event)
    {
        label.setText(event.message);
    }

}
