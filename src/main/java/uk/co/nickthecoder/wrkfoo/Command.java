package uk.co.nickthecoder.wrkfoo;

import javax.swing.Icon;
import javax.swing.JComponent;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.Parameter;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.option.Options;

/**
 * A Command performs some work via a {@link Task}. The Task is defined as usual by a set of {@link Parameter}s.
 * The results of the task are a list of objects (of generic type R).
 * Typically these are held in a List, and use a {@link ListTableModel}.
 * 
 * Commands also have the meta-data needed to display these results in a GUI table,
 * i.e. it defines the columns, and any special cell renderers etc.
 * 
 */
public interface Command
{
    public void postCreate();
    
    public Task getTask();

    public String getName();

    public String getShortTitle();

    public String getTitle();
    
    public String getLongTitle();

    public Icon getIcon();

    public GroupParameter getParameters();

    public void attachTo(CommandTab tab);

    public void detach();

    public void clearResults();

    public CommandTab getCommandTab();


    public void updateResults();

    // public CommandTableModel<R> getTableModel();

    public JComponent createResultsComponent();

    public ParametersPanel createParametersPanel();

    public CommandPanel getCommandPanel();

    public void go();

    public void stop();

    public Options getOptions();

    public Command duplicate();
    
    public void addCommandListener( CommandListener cl );

    public void removeCommandListener( CommandListener cl );
}
