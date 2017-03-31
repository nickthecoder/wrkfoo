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
    public void postCreate();

    public Task getTask();

    public String getShortTitle();

    public String getLongTitle();

    public Icon getIcon();
    
    public void updateResults();

    public S getResultsPanel();

    public ParametersPanel createParametersPanel();

    public ToolPanel getToolPanel();

    public ToolTab getToolTab();

    public boolean isRerunnable();

    public void go();

    public void stop();

    public Options getOptions();

    public Tool<S> duplicate();

    /**
     * A String which is used to create a new instance of this Tool. For java classes, this is simply
     *  the fully qualified class name. For a groovy script, it is the path to the groovy file.
     */
    public String getCreationString();
}
