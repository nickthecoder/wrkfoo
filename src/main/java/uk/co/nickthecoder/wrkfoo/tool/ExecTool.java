package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.wrkfoo.AbstractTextTool;

public class ExecTool extends AbstractTextTool<SimpleExecTask>
{
    public ExecTool(SimpleExecTask task)
    {
        super(task);
    }

    public ExecTool(Exec exec)
    {
        this(new SimpleExecTask(exec));
    }
    
    public ExecTool dir( File directory )
    {
        getTask().getExec().dir(directory);
        return this;
    }
}
