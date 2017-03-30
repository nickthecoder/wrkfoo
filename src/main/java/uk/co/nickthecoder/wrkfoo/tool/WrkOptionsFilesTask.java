package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.ChoiceParameter;
import uk.co.nickthecoder.jguifier.util.FileLister;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptionsFilesTask.WrkOptionsFile;

public class WrkOptionsFilesTask extends Task implements ListResults<WrkOptionsFile>
{
    ChoiceParameter<File> directory = Resources.getInstance().createOptionsDirectoryChoice();

    private List<WrkOptionsFile> results;

    public WrkOptionsFilesTask()
    {
        addParameters(directory);
    }

    @Override
    public List<WrkOptionsFile> getResults()
    {
        return results;
    }

    @Override
    public void body()
    {
        FileLister fileLister = new FileLister().extension("json");
        List<File> files = fileLister.listFiles(directory.getValue());

        results = new ArrayList<>();
        for (File file : files) {
            results.add(new WrkOptionsFile(file));
        }
    }

    public WrkOptions wrkOptions(OptionsData optionsData)
    {
        // TODO This is broken!
        WrkOptions wrkOptions = new WrkOptions();
        return wrkOptions;
    }

    public class WrkOptionsFile extends WrappedFile
    {
        public WrkOptionsFile(File file)
        {
            super(file);
        }

    }
}
