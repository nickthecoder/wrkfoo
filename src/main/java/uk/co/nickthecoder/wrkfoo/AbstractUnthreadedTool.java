package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractUnthreadedTool<T extends Task> extends AbstractTool<T>
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
        focusOnResults(7);

    }

    public void stop()
    {
        throw new RuntimeException("Cannot stop an UnthreadedTool");
    }

}
