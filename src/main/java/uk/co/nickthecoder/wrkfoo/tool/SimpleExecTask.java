package uk.co.nickthecoder.wrkfoo.tool;

import uk.co.nickthecoder.jguifier.util.Exec;

public class SimpleExecTask extends ExecTask
{
    private final Exec exec;

    public SimpleExecTask(Exec exec)
    {
        this.exec = exec;
    }

    @Override
    public Exec getExec()
    {
        return exec;
    }

}
