package uk.co.nickthecoder.wrkfoo;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.wrkfoo.option.Options;

/**
 * A Tool performs some work via a {@link Task}. The Task is defined as usual by a set of {@link Parameter}s.
 * The message of the task are a list of objects (of generic type R).
 * Typically these are held in a List, and use a {@link ListTableModel}.
 * 
 * Tools also have the meta-data needed to display these message in a GUI table,
 * i.e. it defines the columns, and any special cell renderers etc.
 * 
 */
public interface Tool<S extends Results>
{
    public Task getTask();

    public String getShortTitle();

    public String getLongTitle();

    public Icon getIcon();

    public void updateResults();

    public S getResultsPanel();

    public ParametersPanel createParametersPanel();

    public ToolPanel getToolPanel();

    public HalfTab getHalfTab();

    /**
     * Called after a Tool has been fully created, and attached to a HalfTab
     */
    public void attached();

    /**
     * Called after a Tool has been removed from its HalfTab.
     */
    public void detached();

    public boolean isRerunnable();

    public void go();

    public void stop();

    public String getOptionsName();

    public void setOverrideOptionsName(String value);

    public String getOverrideOptionsName();

    public Options getOptions();

    public Tool<S> duplicate();

    /**
     * The tool that should appear when a split is performed.
     * The normal behaviour is to return {@link #duplicate()}.
     * 
     * @return The new Tool which will appear on the right split pane.
     */
    public Tool<?> splitTool(boolean vertical);

    /**
     * A String which is used to create a new instance of this Tool. For java classes, this is simply
     * the fully qualified class name. For a groovy script, it is the path to the groovy file.
     */
    public String getCreationString();
}
