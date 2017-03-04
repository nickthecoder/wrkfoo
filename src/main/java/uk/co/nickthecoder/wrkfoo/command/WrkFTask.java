package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.util.FileListerTask;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.command.WrkFTask.WrkFWrappedFile;

public class WrkFTask extends FileListerTask implements ListResults<WrkFWrappedFile>
{
    private List<WrkFWrappedFile> wrappedResults;

    @Override
    public List<WrkFWrappedFile> getResults()
    {
        return wrappedResults;
    }

    @Override
    public void body()
    {
        super.body();
        wrappedResults = new ArrayList<WrkFWrappedFile>();

        for (File file : results) {
            wrappedResults.add(new WrkFWrappedFile(file));
        }
    }

    public class WrkFWrappedFile extends WrappedFile
    {

        public WrkFWrappedFile(File file)
        {
            super(file);
        }

        public File getBase()
        {
            return directory.getValue();
        }
    }
}
