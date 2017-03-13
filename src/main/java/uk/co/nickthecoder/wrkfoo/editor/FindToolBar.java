package uk.co.nickthecoder.wrkfoo.editor;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.DocumentRange;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaHighlighter;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class FindToolBar extends JPanel
{
    private TextEditorPane textEditorPane;

    private SearchContext context;

    private JTextField textField;

    private JButton prevButton;

    private JButton nextButton;

    private JLabel label;

    private JCheckBox matchWholeWord;

    private JCheckBox matchRegex;

    private JCheckBox matchCase;

    public FindToolBar(TextEditorPane tep)
    {
        this.textEditorPane = tep;
        this.context = new SearchContext();

        this.add(new JLabel("Find"));

        textField = new JTextField(15);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(textField);

        textField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                performSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                performSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                // performSearch();
            }

        });
        // TODO Listen for ENTER key

        ActionBuilder builder = new ActionBuilder(this);
        prevButton = builder.name("findPrev").shortcut("ctrl shift G").buildButton();
        nextButton = builder.name("findNext").shortcut("ctrl G").buildButton();

        this.add(prevButton);
        this.add(nextButton);

        label = new JLabel("");
        this.add(label);

        matchRegex = builder.name("matchCase").label("Match Case").buildCheckBox();
        this.add(matchRegex);

        matchWholeWord = builder.name("matchWholeWord").label("Whole Whole Word").buildCheckBox();
        this.add(matchWholeWord);

        matchRegex = builder.name("matchRegex").label("Regex").buildCheckBox();
        this.add(matchRegex);

    }

    public void onMatchCase()
    {
        context.setMatchCase(matchCase.isSelected());
    }

    public void onMatchWholeWord()
    {
        context.setWholeWord(matchWholeWord.isSelected());
    }

    public void onMatchRegex()
    {
        context.setRegularExpression(matchRegex.isSelected());
    }

    public void setSearchContext(SearchContext value)
    {
        this.context = value;
    }

    public SearchContext getSearchContext()
    {
        return this.context;
    }

    public String getFindText()
    {
        return textField.getText();
    }

    public void onFindPrev()
    {
        context.setSearchForward(false);
        if (searchResult.getMarkedCount() > 0) {
            if (currentMatchNumber == 1) {
                textEditorPane.setCaretPosition(textEditorPane.getDocument().getLength());
            }
            performSearch();
        }
    }

    public void onFindNext()
    {
        context.setSearchForward(true);
        if (searchResult.getMarkedCount() > 0) {
            if (currentMatchNumber == searchResult.getMarkedCount()) {
                textEditorPane.setCaretPosition(0);
            }
            
            performSearch();
        }
    }

    private SearchResult searchResult;

    private int currentMatchNumber;

    private void performSearch()
    {
        context.setSearchFor(getFindText());
        SearchEngine.markAll(textEditorPane, context);

        searchResult = SearchEngine.find(textEditorPane, context);

        currentMatchNumber = getMatchNumber();
        if (currentMatchNumber > 0) {
            label.setText("" + currentMatchNumber + " of " + searchResult.getMarkedCount());
        } else {
            label.setText("");
        }
    }

    // This seams to be the easiest way to find which match number we are on. Grr.
    private int getMatchNumber()
    {
        RSyntaxTextAreaHighlighter highlighter = (RSyntaxTextAreaHighlighter) textEditorPane.getHighlighter();

        if (searchResult.getMarkedCount() > 0) {
            DocumentRange currentRange = searchResult.getMatchRange();

            List<DocumentRange> ranges = highlighter.getMarkAllHighlightRanges();
            int i = 0;
            if (currentRange != null) {
                for (DocumentRange range : ranges) {
                    if (range.getStartOffset() == currentRange.getStartOffset()) {
                        return i + 1;
                    }
                    i++;
                }
            }
        }
        return 0;
    }

    public void setVisible(boolean show)
    {
        super.setVisible(show);
        if (show) {
            matchRegex.setSelected(context.isRegularExpression());
            matchWholeWord.setSelected(context.getWholeWord());
            textField.selectAll();
            textField.requestFocus();
        }
    }
}
