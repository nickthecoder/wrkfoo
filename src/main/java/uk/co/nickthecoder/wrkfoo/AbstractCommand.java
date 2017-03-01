package uk.co.nickthecoder.wrkfoo;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.Parameter;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.ValueParameter;

public abstract class AbstractCommand<T extends Task, R> implements Command<R>
{
    protected Columns<R> columns;

    public T task;

    private CommandTab commandTab;

    public AbstractCommand(T task)
    {
        this.task = task;
    }

    @Override
    public void postCreate()
    {
        // Do nothing
    }

    @Override
    public T getTask()
    {
        return task;
    }

    @Override
    public String getTitle()
    {
        return task.getName();
    }

    @Override
    public String getShortTitle()
    {
        return getTitle();
    }

    @Override
    public String getLongTitle()
    {
        return getTitle();
    }

    @Override
    public String getName()
    {
        return this.getClass().getSimpleName();
    }

    @Override
    public Icon getIcon()
    {
        return null;
    }

    @Override
    public GroupParameter getParameters()
    {
        return task.getParameters();
    }

    public Columns<R> getColumns()
    {
        if (columns == null) {
            columns = createColumns();
        }
        return columns;
    }

    protected abstract Columns<R> createColumns();

    @Override
    public SimpleTable<R> createTable()
    {
        SimpleTable<R> result = getColumns().createTable(getTableModel());
        return result;
    }

    @Override
    public void attachTo(CommandTab tab)
    {
        assert (this.commandTab == null);

        this.commandTab = tab;
    }

    @Override
    public void detach()
    {
        this.commandTab = null;
        this.clearResults();
        this.commandPanel = null;
        this.columns = null;
    }

    @Override
    public CommandTab getCommandTab()
    {
        return commandTab;
    }

    private CommandPanel<R> commandPanel;

    @Override
    public ParametersPanel createParametersPanel()
    {
        ParametersPanel pp = new ParametersPanel();
        pp.addParameters(getTask().getParameters());
        return pp;

    }

    public CommandPanel<R> getCommandPanel()
    {
        if (commandPanel == null) {
            commandPanel = new CommandPanel<R>(this);
            commandPanel.postCreate();
        }

        return commandPanel;
    }

    /**
     * Routed through commandTab, so that it can record the history.
     */
    @Override
    public void go()
    {
        if (commandTab != null) {
            commandTab.go(this);
        } else {
            System.out.println("Not attached to a AbstractCommand");
            task.run();
        }
        updateResults();
    }

    public abstract void updateResults();

    private Options options;

    public Options getOptions()
    {
        if (options == null) {

            OptionsGroup og = new OptionsGroup();
            String name = optionsName();
            if (name != null) {
                og.add(Resources.instance.readOptions(name));
            }
            og.add(Resources.instance.globalOptions());
            options = og;
        }
        return options;
    }

    protected String optionsName()
    {
        return null;
    }

    /**
     * A convenience method for {@link GroovyOption}s to change a parameter, and return the same Command.
     * 
     * @param name
     *            The name of the parameter
     * @param value
     *            The new value for the parameter
     * @return this
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public AbstractCommand parameter(String name, Object value)
    {
        Parameter p = getTask().findParameter(name);
        ValueParameter vp = (ValueParameter) p;

        vp.setValue(value);
        return this;
    }
}
