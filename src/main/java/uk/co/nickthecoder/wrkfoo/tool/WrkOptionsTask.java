package uk.co.nickthecoder.wrkfoo.tool;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.option.OptionsData.OptionData;

public class WrkOptionsTask extends Task implements ListResults<OptionData>
{
    private List<OptionsData.OptionData> results;

    public OptionsData optionsData;

    public FileParameter optionsFile = new FileParameter.Builder("optionsFile").mustExist()
        .parameter();

    public WrkOptionsTask()
    {
        addParameters(optionsFile);
    }

    @Override
    public List<OptionData> getResults()
    {
        return results;
    }

    @Override
    public void body()
    {
        optionsData = Resources.instance.readOptionsData(optionsFile.getValue());
        results = new ArrayList<>(optionsData.options);
    }

}
