package uk.co.nickthecoder.wrkfoo;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;

public abstract class TaskCommand<T extends Task, R> implements Command<R>
{
    protected Columns<R> columns;
    
    public T task;

    private CommandTab commandTab;

    private Options options;
    
    public TaskCommand(T task)
    {
        this.task = task;
        this.options = new SimpleOptions();
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
    public String getName()
    {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getTitle()
    {
        return task.getName();
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
    }
    
    @Override
    public CommandTab getCommandTab()
    {
        return commandTab;
    }

    private CommandPanel<R> commandPanel;

    @Override
    public Options getOptions()
    {
        return options;
    }

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
        if ( commandTab != null) {
            commandTab.go( this );
        } else {
            System.out.println( "Not attached to a TaskCommand" );
            task.run();
        }
        updateResults();
    }
    
    public abstract void updateResults();
    
    @Override
    public void defaultAction(R row)
    {
        // Do nothing
    }
}
