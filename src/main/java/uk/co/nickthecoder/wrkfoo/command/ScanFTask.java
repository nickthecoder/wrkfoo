package uk.co.nickthecoder.wrkfoo.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.BooleanParameter;
import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.command.ScanFTask.ScannedDirectory;

public class ScanFTask extends Task implements ListResults<ScannedDirectory>
{

    public FileParameter directory = new FileParameter.Builder("directory").mustExist().directory()
        .description("Starting directory")
        .parameter();

    public BooleanParameter oneFileSystem = new BooleanParameter.Builder("oneFileSystem")
        .description("Skip directories on different file systems")
        .parameter();

    public List<ScannedDirectory> results;

    public ScanFTask()
    {
        addParameters(directory, oneFileSystem);
    }

    @Override
    public void body()
    {
        results = new ArrayList<ScannedDirectory>();

        Exec du = new Exec("du", "--bytes", oneFileSystem.getValue() ? "--one-file-system" : null,
            directory.getValue().getPath());
        
        BufferedReader reader = du.runBuffered();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                int tab = line.indexOf('\t');
                if (tab >= 0) {
                    long size = Long.parseLong(line.substring(0, tab));
                    String path = line.substring(tab + 1);
                    results.add(new ScannedDirectory(path, size));
                } else {
                    System.err.println("Skipping  " + line);
                }
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
    public List<ScannedDirectory> getResults()
    {
        return results;
    }

    public class ScannedDirectory
    {
        final String path;
        final long size;
        File file;

        public ScannedDirectory(String path, long size)
        {
            this.path = path;
            this.size = size;
        }

        public File getFile()
        {
            if (file == null) {
                file = new File(path);
            }
            return file;
        }

    }
}
