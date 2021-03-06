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

import uk.co.nickthecoder.wrkfoo.Focuser;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.AutoComponentUpdater;

public class FindToolBar extends JPanel implements SearcherListener
{
    private Searcher searcher;

    private JTextField textField;

    private JLabel label;

    private JCheckBox matchWholeWord;

    private JCheckBox matchRegex;

    private JCheckBox matchCase;

    public JPanel rightPanel;

    @SuppressWarnings("unused") // Keep a reference to stop it being garbage collected.
    private AutoComponentUpdater componentUpdater;

    public FindToolBar(Searcher s)
    {
        this.searcher = s;
        this.setLayout(new BorderLayout());

        JPanel squashed = new JPanel();
        squashed.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
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

        ActionBuilder builder = new ActionBuilder(searcher).component(this);

        JButton nextButton = builder.name("find.findNext").buildButton();
        JButton prevButton = builder.name("find.findPrev").buildButton();

        builder.component(textField).condition(WHEN_FOCUSED);
        builder.name("find.go").buildShortcut();

        builder = new ActionBuilder(this);

        squashed.add(prevButton);
        squashed.add(nextButton);

        label = new JLabel("");
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        this.add(label);

        rightPanel = new JPanel();
        this.add(rightPanel, BorderLayout.EAST);

        matchCase = builder.name("find.matchCase").label("Match Case").buildCheckBox();
        rightPanel.add(matchCase);

        matchWholeWord = builder.name("find.matchWholeWord").label("Whole Whole Word").buildCheckBox();
        rightPanel.add(matchWholeWord);

        matchRegex = builder.name("find.matchRegex").label("Regex").buildCheckBox();
        rightPanel.add(matchRegex);

        searcher.addSearcherListener(this);

        componentUpdater = new AutoComponentUpdater(getClass().getSimpleName())
        {
            @Override
            protected void autoUpdate()
            {
                prevButton.setEnabled(searcher.getMatchCount() > 0);
                nextButton.setEnabled(searcher.getMatchCount() > 0);
            }
        };
    }

    public JTextField getTextField()
    {
        return textField;
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
            Focuser.focusLater("FindToolBar.setVisible", textField, 8);
            searcher.setSearchText(textField.getText());
        }
    }

    @Override
    public void searched(SearcherEvent event)
    {
        label.setText(event.message);
    }

}
