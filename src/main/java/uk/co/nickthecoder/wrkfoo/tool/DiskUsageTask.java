package uk.co.nickthecoder.wrkfoo.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.BooleanParameter;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.tool.DiskUsageTask.ScannedDirectory;

public class DiskUsageTask extends Task implements ListResults<ScannedDirectory>, Stoppable
{
    private Exec du;

    public FileParameter directory = new FileParameter.Builder("directory").mustExist().directory()
        .description("Starting directory")
        .parameter();

    public BooleanParameter oneFileSystem = new BooleanParameter.Builder("oneFileSystem")
        .description("Skip directories on different file systems")
        .parameter();

    public List<ScannedDirectory> results;

    public DiskUsageTask()
    {
        addParameters(directory, oneFileSystem);
    }

    @Override
    public void body() throws Exception
    {
        stopping = false;
        results = new ArrayList<>();

        du = new Exec("du", "--bytes", oneFileSystem.getValue() ? "--one-file-system" : null,
            directory.getValue().getPath());

        BufferedReader reader = du.runBuffered();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (stopping) {
                    break;
                }

                int tab = line.indexOf('\t');
                if (tab >= 0) {
                    long size = Long.parseLong(line.substring(0, tab));
                    String path = line.substring(tab + 1);
                    results.add(new ScannedDirectory(new File(path), size));
                } else {
                    System.err.println("Skipping  " + line);
                }
            }

        } finally {

            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public List<ScannedDirectory> getResults()
    {
        return results;
    }

    public class ScannedDirectory extends WrappedFile
    {
        final long size;

        public ScannedDirectory(File file, long size)
        {
            super(file);
            this.size = size;
        }
    }

    private boolean stopping = false;

    @Override
    public void stop()
    {
        stopping = true;
        if (du != null) {
            du.stop();
        }
    }
}
