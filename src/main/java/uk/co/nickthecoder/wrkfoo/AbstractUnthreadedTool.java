package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractUnthreadedTool<T extends Task> extends AbstractTool<T>
{
    private boolean running = false;

    public AbstractUnthreadedTool(T task)
    {
        super(task);
    }

    @Override
    public void go()
    {
        running = true;
        try {
            task.run();
            updateResults();
            getToolPanel().getSplitPane().showLeft();
            focusOnResults(7);
        } finally {
            running = false;
        }
    }

    public void stop()
    {
        throw new RuntimeException("Cannot stop an UnthreadedTool");
    }

    public boolean isRunning()
    {
        return running;
    }
}
