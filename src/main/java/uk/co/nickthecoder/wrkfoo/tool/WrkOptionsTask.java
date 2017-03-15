package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.BooleanParameter;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.option.OptionsData.OptionData;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptions.OptionRow;

public class WrkOptionsTask extends Task implements ListResults<OptionRow>
{
    private List<OptionRow> results;

    public OptionsData optionsData;

    public FileParameter optionsFile = new FileParameter.Builder("optionsFile").mustExist()
        .parameter();

    public BooleanParameter showIncludes = new BooleanParameter.Builder("showIncludes")
        .value(true).parameter();

    //public BooleanParameter showGlobals = new BooleanParameter.Builder("showGlobals")
    //    .value(false).parameter();

    public WrkOptionsTask()
    {
        addParameters(optionsFile, showIncludes ); //, showGlobals);
    }

    @Override
    public List<OptionRow> getResults()
    {
        return results;
    }

    @Override
    public void body()
    {
        optionsData = Resources.instance.readOptionsData(optionsFile.getValue());
        results = new ArrayList<>();

        added = new HashSet<>();
        add(optionsData);

        // TODO Once resources only has a single globals file, then finish this.
        //if (showGlobals.getValue()) {
        //}

    }

    private Set<File> added;

    private void add(OptionsData optionsData)
    {
        for (OptionData data : optionsData.options) {
            results.add(new OptionRow(data, optionsData.file));
        }

        added.add(optionsData.file);

        if (showIncludes.getValue()) {

            for (String include : optionsData.include) {
                OptionsData child = Resources.instance.readOptionsData(Resources.instance.getOptionsFile(include));
                if (!added.contains(child.file)) {
                    add(child);
                }
            }
        }
    }

}
