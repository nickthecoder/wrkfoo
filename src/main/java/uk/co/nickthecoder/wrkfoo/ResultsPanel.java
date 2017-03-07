package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import uk.co.nickthecoder.jguifier.ParametersPanel;

/**
 * The {@link CommandPanel} is made up of two halves, the {@link ParametersPanel} on the right, and this
 * ResultsPanel on the left.
 * For {@link TableCommand}s, this will be a {@link TableResultsPanel}, and for text commands, this
 * will be {@link TextResultsPanel}.
 * Each implementation of ResultsPanel has a corresponding interface, which gives access to the data used to populate
 * the GUI. For example, {@link TextResults} and {@link ListResults}. 
 */
public class ResultsPanel extends JPanel
{
    public ResultsPanel()
    {
        this.setLayout(new BorderLayout());
    }
}
