package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;

import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.wrkfoo.AbstractTextCommand;

public class ExecCommand extends AbstractTextCommand<SimpleExecTask>
{
    public ExecCommand(SimpleExecTask task)
    {
        super(task);
    }

    public ExecCommand(Exec exec)
    {
        super(new SimpleExecTask(exec));
    }
    
    public ExecCommand dir( File directory )
    {
        getTask().getExec().dir(directory);
        return this;
    }
}
