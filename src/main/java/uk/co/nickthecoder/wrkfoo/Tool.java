package uk.co.nickthecoder.wrkfoo;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.Parameter;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.option.Options;

/**
 * A Tool performs some work via a {@link Task}. The Task is defined as usual by a set of {@link Parameter}s.
 * The results of the task are a list of objects (of generic type R).
 * Typically these are held in a List, and use a {@link ListTableModel}.
 * 
 * Tools also have the meta-data needed to display these results in a GUI table,
 * i.e. it defines the columns, and any special cell renderers etc.
 * 
 */
public interface Tool
{
    public void postCreate();
    
    public Task getTask();

    public String getName();

    public String getShortTitle();

    public String getTitle();
    
    public String getLongTitle();

    public Icon getIcon();

    public GroupParameter getParameters();

    public void attachTo(ToolTab tab);

    public void detach();

    public ToolTab getToolTab();


    public void updateResults();

    public ResultsPanel createResultsComponent();

    public ParametersPanel createParametersPanel();

    public ToolPanel getToolPanel();

    public void go();

    public void stop();

    public Options getOptions();

    public Tool duplicate();
    
    public void addToolListener( ToolListener cl );

    public void removeToolListener( ToolListener cl );
}
