package uk.co.nickthecoder.wrkfoo;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.Parameter;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;

/**
 * A Command performs some work via a {@link Task}. The Task is defined as usual by a set of {@link Parameter}s.
 * The results of the task are a list of objects (of generic type R).
 * Typically these are held in a List, and use a {@link ListTableModel}.
 * 
 * Commands also have the meta-data needed to display these results in a GUI table,
 * i.e. it defines the columns, and any special cell renderers etc.
 * 
 * @param <R>
 *            The type of object in
 */
public interface Command<R>
{
    public void postCreate();
    
    public Task getTask();

    public String getName();

    public String getTitle();

    public Icon getIcon();

    public GroupParameter getParameters();

    public void attachTo(CommandTab tab);

    public void detach();

    public void clearResults();

    public CommandTab getCommandTab();

    public Columns<R> getColumns();

    public void updateResults();

    public CommandTableModel<R> getTableModel();

    public SimpleTable<R> createTable();

    public ParametersPanel createParametersPanel();

    public CommandPanel<R> getCommandPanel();

    public void defaultAction(R row);

    public void go();

    public Options getOptions();

}
