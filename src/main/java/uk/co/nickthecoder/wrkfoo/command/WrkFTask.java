package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.util.List;

import uk.co.nickthecoder.jguifier.util.FileListerTask;
import uk.co.nickthecoder.wrkfoo.ListResults;

public class WrkFTask extends FileListerTask implements ListResults<File>
{
    @Override
    public List<File> getResults()
    {
        return results;
    }
}
