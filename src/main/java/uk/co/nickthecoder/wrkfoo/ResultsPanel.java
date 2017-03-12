package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import uk.co.nickthecoder.jguifier.ParametersPanel;

/**
 * The {@link ToolPanel} is made up of two halves, the {@link ParametersPanel} on the right, and this
 * ResultsPanel on the left.
 * For {@link TableTool}s, this will be a {@link TableResultsPanel}, and for text tools, this
 * will be {@link TextResultsPanel}.
 * Each implementation of ResultsPanel has a corresponding interface, which gives access to the data used to populate
 * the GUI. For example, {@link TextResults} and {@link ListResults}.
 */
public class ResultsPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    public ResultsPanel()
    {
        this.setLayout(new BorderLayout());
    }
}
