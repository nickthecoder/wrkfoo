package uk.co.nickthecoder.wrkfoo;

import javax.swing.JComponent;

import uk.co.nickthecoder.jguifier.ParametersPanel;

/**
 * The {@link ToolPanel} is made up of two halves, the {@link ParametersPanel} on the right, and this
 * ResultsPanel on the left.
 * For {@link TableTool}s, this will be a {@link TableResults}, and for text tools, this
 * will be {@link TextResultsPanel}.
 * Each implementation of ResultsPanel has a corresponding interface, which gives access to the data used to populate
 * the GUI. For example, {@link TextResults} and {@link ListResults}.
 */
public interface Results
{
    public JComponent getComponent();

    public Tool<?> getTool();
    
    public default JComponent getFocusComponent()
    {
        try {
            Focuser.log("Results panel default implementation returning MainWindow's option field");
            return getTool().getToolPanel().getToolBar().getOptionsTextField();
        } catch (Exception e) {
            Focuser.log("Failed to focus on options field for component " + getComponent());
            return null;
        }
    }
}
