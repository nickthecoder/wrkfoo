package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractCommand<T extends Task & Results<R>, R> implements Command<R>
{
    public T task;

    public AbstractCommand(T task, String... columnNames)
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

    @Override
    public void run()
    {
        task.run();
    }

    public Results<R> getResults()
    {
        return task;
    }

}
