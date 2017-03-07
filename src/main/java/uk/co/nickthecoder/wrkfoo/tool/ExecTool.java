package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.wrkfoo.AbstractTextTool;

public class ExecTool extends AbstractTextTool<SimpleExecTask>
{
    private boolean rerunnable = true;

    public ExecTool(SimpleExecTask task)
    {
        super(task);
    }

    public ExecTool(Exec exec)
    {
        this(new SimpleExecTask(exec));
    }

    public ExecTool dir(File directory)
    {
        getTask().getExec().dir(directory);
        return this;
    }

    @Override
    public boolean isRerunnable()
    {
        return rerunnable;
    }

    /**
     * Prevents this tool from being re-run by going back/forward in history.
     * Doesn't prevent the task from being re-run manually by clicking the Go button.
     * 
     * @return this
     */
    public ExecTool once()
    {
        this.rerunnable = false;
        return this;
    }
}
