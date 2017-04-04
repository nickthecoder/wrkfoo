package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.util.FileLister;
import uk.co.nickthecoder.wrkfoo.ListResults;

public class PlacesChoicesTask extends Task implements ListResults<WrappedFile>
{
    private List<WrappedFile> results;

    public FileParameter directory = new FileParameter.Builder("directory").directory().mustExist()
        .parameter();

    public PlacesChoicesTask()
    {
        super();
        addParameters(directory);
    }

    @Override
    public void body() throws Exception
    {
        results = new ArrayList<>();

        FileLister lister = new FileLister();

        for (File file : lister.listFiles(directory.getValue())) {
            results.add(new WrappedFile(file));
        }
    }

    @Override
    public List<WrappedFile> getResults()
    {
        return results;
    }
}
