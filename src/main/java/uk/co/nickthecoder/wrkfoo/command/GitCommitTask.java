package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;

import uk.co.nickthecoder.jguifier.BooleanParameter;
import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.util.Exec;

public class GitCommitTask extends ExecTask
{
    public FileParameter directory = new FileParameter.Builder("directory").directory().mustExist()
        .value(new File(".")).parameter();

    public BooleanParameter all = new BooleanParameter.Builder("all")
        .parameter();


    public GitCommitTask()
    {
        super();
        addParameters(directory, all);
    }
    
    @Override
    public Exec getExec()
    {
        Exec exec = new Exec("git", "commit").stdout().dir(directory.getValue());
        if (all.getValue()) {
            exec.add( "-a" );
        }

        return exec;
    }

}
