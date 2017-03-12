package uk.co.nickthecoder.wrkfoo.tool;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.wrkfoo.TextResults;

public abstract class ExecTask extends Task implements TextResults, Stoppable
{
    public String results;

    private Exec exec;

    public ExecTask()
    {
        super();
    }

    public abstract Exec getExec();
    
    @Override
    public void body()
    {
        exec = getExec();
        exec.stdout();
        exec.mergeStderr();
        exec.run();
        results = exec.getStdout().toString();
    }

    @Override
    public void stop()
    {
        if (exec != null) {
            exec.stop();
        }
    }

    @Override
    public String getResults()
    {
        return results;
    }
}
