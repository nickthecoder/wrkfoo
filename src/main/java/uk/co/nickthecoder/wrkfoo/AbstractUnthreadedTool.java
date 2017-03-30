package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractUnthreadedTool<S extends Results, T extends Task>
    extends AbstractTool<S, T>
{
    public AbstractUnthreadedTool(T task)
    {
        super(task);
    }

    @Override
    public void go()
    {
        task.run();
        updateResults();
        getToolPanel().getSplitPane().showLeft();
    }

    public void stop()
    {
        throw new RuntimeException("Cannot stop an UnthreadedTool");
    }

}
