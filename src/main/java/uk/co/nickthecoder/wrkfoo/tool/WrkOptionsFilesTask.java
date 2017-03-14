package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.util.FileLister;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptionsFilesTask.WrkOptionsFile;

public class WrkOptionsFilesTask extends Task implements ListResults<WrkOptionsFile>
{
    FileParameter directory = new FileParameter.Builder("directory").directory().mustExist()
        .value(Resources.instance.getOptionsDirectory())
        .parameter();

    private List<WrkOptionsFile> results;

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
        WrkOptions wrkOptions = new WrkOptions();
        wrkOptions.getTask().optionsFile.setValue(optionsData.file);
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
