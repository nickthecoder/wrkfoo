package uk.co.nickthecoder.wrkfoo.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import uk.co.nickthecoder.jguifier.parameter.BooleanParameter;
import uk.co.nickthecoder.jguifier.parameter.GroupParameter;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.jguifier.ParameterListener;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class ReplaceDialog extends JDialog implements ParameterListener, SearcherListener
{
    private static final long serialVersionUID = 1L;

    public StringParameter search = new StringParameter.Builder("searchFor")
        .optional().parameter();

    public StringParameter replace = new StringParameter.Builder("replaceWith")
        .optional().parameter();

    public BooleanParameter matchCase = new BooleanParameter.Builder("matchCase")
        .value(false).parameter();

    public BooleanParameter matchWord = new BooleanParameter.Builder("matchWord")
        .description("match entire words only")
        .value(false).parameter();

    public BooleanParameter regex = new BooleanParameter.Builder("regex")
        .description("match using a regular expression")
        .value(false).parameter();

    public BooleanParameter backwards = new BooleanParameter.Builder("searchBackwards")
        .value(false).parameter();

    private Searcher searcher;

    private ParametersPanel parametersPanel;

    private GroupParameter group;

    private JButton closeButton;

    private JButton replaceAllButton;

    private JButton replaceButton;

    private JButton replaceFindButton;

    private JButton findButton;

    private JLabel resultsLabel;

    public ReplaceDialog(Frame owner, Searcher searcher)
    {
        super(owner, "Replace", JDialog.ModalityType.MODELESS);

        this.searcher = searcher;
        this.searcher.addSearcherListener(this);

        group = new GroupParameter("parameters");
        group.addChildren(search, replace, matchCase, matchWord, regex, backwards);

        search.addListener(this);
        matchCase.addListener(this);
        matchWord.addListener(this);
        regex.addListener(this);
        backwards.addListener(this);

        parametersPanel = new ParametersPanel();
        parametersPanel.addParameters(group);

        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());
        main.add(parametersPanel, BorderLayout.CENTER);

        resultsLabel = new JLabel();
        resultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        main.add(resultsLabel, BorderLayout.SOUTH);

        this.setLayout(new BorderLayout());
        this.add(main, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());

        ActionBuilder builder = new ActionBuilder(this);

        closeButton = builder.name("close").label("Close").icon("close.png").buildButton();
        replaceAllButton = builder.name("replaceAll").label("Replace All").buildButton();
        replaceButton = builder.name("replace").label("Replace").icon("editReplace.png").buildButton();
        replaceFindButton = builder.name("replaceFind").label("Replace/Find").buildButton();
        findButton = builder.name("find").label("Find").icon("editFind.png").buildButton();

        buttons.add(closeButton);
        buttons.add(replaceAllButton);
        buttons.add(replaceButton);
        buttons.add(replaceFindButton);
        buttons.add(findButton);

        builder.name("escape").shortcut("ESCAPE").buildShortcut();

        this.add(buttons, BorderLayout.SOUTH);

    }

    public void onClose()
    {
        setVisible(false);
    }

    public void onReplaceAll()
    {
        searcher.replaceAll(search.getValue(), replace.getValue());
    }

    public void onReplace()
    {
        searcher.replace(search.getValue(), replace.getValue());
    }

    public void onReplaceFind()
    {
        onReplace();
        onFind();
    }

    public void onFind()
    {
        searcher.onFindNext();
    }

    public void onEscape()
    {
        setVisible(false);
    }

    @Override
    public void changed(Parameter param)
    {
        if (param == search) {
            searcher.setSearchText(search.getValue());
        } else {

            searcher.context.setSearchForward(!backwards.getValue());
            searcher.context.setRegularExpression(regex.getValue());
            searcher.context.setMatchCase(matchCase.getValue());
            searcher.context.setWholeWord(matchWord.getValue());
        }
    }

    @Override
    public void searched(SearcherEvent event)
    {
        findButton.setEnabled(searcher.getMatchCount() > 0);
        replaceButton.setEnabled(searcher.getCurrentMatchNumber() > 0);
        replaceFindButton.setEnabled(searcher.getCurrentMatchNumber() > 0);
        replaceAllButton.setEnabled(searcher.getMatchCount() > 0);

        resultsLabel.setText(event.message);
    }

    @Override
    public void setVisible(boolean value)
    {
        super.setVisible(value);
        if (value == false) {
            searcher.clearMarks();
        } else {
            backwards.setValue(!searcher.context.getSearchForward());
            regex.setValue(searcher.context.isRegularExpression());
            matchCase.setValue(searcher.context.getMatchCase());
            matchWord.setValue(searcher.context.getWholeWord());
            searcher.markMatches();
        }
    }
}
