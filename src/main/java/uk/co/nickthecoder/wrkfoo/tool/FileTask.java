package uk.co.nickthecoder.wrkfoo.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.wrkfoo.ListResults;

public abstract class FileTask extends Task implements ListResults<RelativePath>, Stoppable
{
    protected Exec exec;

    public FileParameter directory = new FileParameter.Builder("directory").mustExist().directory()
        .description("Starting directory")
        .parameter();

    public List<RelativePath> results;

    public abstract Exec getExec();

    public FileTask()
    {
    }

    @Override
    public void body()
    {
        stopping = false;
        results = new ArrayList<>();

        exec = getExec().dir(directory.getValue());

        BufferedReader reader = exec.runBuffered();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (stopping) {
                    break;
                }
                results.add(new RelativePath(line)
                {
                    @Override
                    public File getBase()
                    {
                        return directory.getValue();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            reader.close();
        } catch (IOException e) {
        }
    }

    @Override
    public List<RelativePath> getResults()
    {
        return results;
    }

    private boolean stopping = false;

    @Override
    public void stop()
    {
        stopping = true;
        if (exec != null) {
            exec.stop();
        }
    }
}
