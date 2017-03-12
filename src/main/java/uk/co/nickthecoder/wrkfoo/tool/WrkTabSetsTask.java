package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Parameter;
import uk.co.nickthecoder.jguifier.util.FileListerTask;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.TabSetData;
import uk.co.nickthecoder.wrkfoo.tool.WrkTabSetsTask.WrkTabSetsFile;

public class WrkTabSetsTask extends FileListerTask implements ListResults<WrkTabSetsFile>
{
    public List<WrkTabSetsFile> wrappedResults;

    public WrkTabSetsTask()
    {
        super();
        for (Parameter parameter : getParameters().getChildren()) {
            parameter.visible = false;
        }

        directory.setValue(Resources.instance.getTabsDirectory());
        directory.visible = true;

        fileExtensions.setValue("json");
        fileExtensions.visible = true;
    }

    @Override
    public List<WrkTabSetsFile> getResults()
    {
        return wrappedResults;
    }

    @Override
    public void body()
    {
        super.body();

        wrappedResults = new ArrayList<>();
        for (File file : results) {
            WrkTabSetsFile wrapped = new WrkTabSetsFile(file);
            TabSetData tsd = TabSetData.load(file);
            wrapped.description = tsd.description;

            wrappedResults.add(wrapped);
        }

    }

    public class WrkTabSetsFile extends WrappedFile
    {
        public String description;

        public WrkTabSetsFile(File file)
        {
            super(file);
        }

    }
}
