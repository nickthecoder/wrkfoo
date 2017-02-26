package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;

public abstract class TaskCommand<T extends Task & Results<R>, R> implements Command<R>
{
    public T task;

    private CommandTab commandTab;

    public TaskCommand(T task)
    {
        this.task = task;
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
    public GroupParameter getParameters()
    {
        return task.getParameters();
    }

    @Override
    public void attachTo(CommandTab tab)
    {
        assert (this.commandTab == null);

        this.commandTab = tab;
    }

    public CommandTab getCommandTab()
    {
        return commandTab;
    }

    private CommandPanel<R> commandPanel;

    @Override
    public Options getOptions()
    {
        // TODO Auto-generated method stub
        return null;
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
            commandTab.go();
        } else {
            task.run();
        }
    }

    public Results<R> getResults()
    {
        return task;
    }
}
