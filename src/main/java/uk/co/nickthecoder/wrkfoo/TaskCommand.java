package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;

public abstract class TaskCommand<T extends Task & Results<R>, R> implements Command<R>
{
    public T task;

    public TaskCommand(T task, String... columnNames)
    {
        this.task = task;
    }

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


    private CommandPanel<R> commandPanel;
    
    @Override
    public ParametersPanel createParametersPanel()
    {
        ParametersPanel pp = new ParametersPanel();
        pp.addParameters(getTask().getParameters());
        return pp;

    }

    @Override
    public CommandPanel<R> getCommandPanel()
    {
        if ( commandPanel == null ) {
            commandPanel = new CommandPanel<R>(this);
        }
        return commandPanel;

    }
    @Override
    public void go()
    {
        task.run();
        System.out.println("Completed run");

        commandPanel.refresh();
    }

    public Results<R> getResults()
    {
        return task;
    }

}
