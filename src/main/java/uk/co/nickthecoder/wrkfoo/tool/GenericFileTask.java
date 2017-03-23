package uk.co.nickthecoder.wrkfoo.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.wrkfoo.ListResults;

public abstract class GenericFileTask<R extends RelativePath> extends Task implements ListResults<R>, Stoppable
{
    protected Exec exec;

    public FileParameter directory = new FileParameter.Builder("directory").mustExist().directory()
        .description("Starting directory")
        .parameter();

    public List<R> results;

    public abstract Exec getExec();

    public GenericFileTask()
    {
    }

    BufferedReader reader;

    @Override
    public void pre() throws IOException
    {
        stopping = false;
        results = new ArrayList<>();

        exec = getExec().dir(directory.getValue());

        reader = exec.runBuffered();
    }

    @Override
    public void body()
    {
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (stopping) {
                    break;
                }
                results.add(parseLine(line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract R parseLine(String line);

    @Override
    public void post()
    {
        try {
            reader.close();
        } catch (IOException e) {
        }
    }

    @Override
    public List<R> getResults()
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
