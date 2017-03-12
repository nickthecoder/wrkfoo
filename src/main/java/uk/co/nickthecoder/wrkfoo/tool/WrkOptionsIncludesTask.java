package uk.co.nickthecoder.wrkfoo.tool;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;

public class WrkOptionsIncludesTask extends Task implements ListResults<String>
{
    private List<String> results;

    public OptionsData optionsData;

    public FileParameter optionsFile = new FileParameter.Builder("optionsFile").mustExist()
        .parameter();

    public WrkOptionsIncludesTask()
    {
        addParameters( optionsFile );
    }
    
    @Override
    public List<String> getResults()
    {
        return results;
    }

    @Override
    public void body()
    {
        optionsData = Resources.instance.readOptionsData(optionsFile.getValue());
        results = new ArrayList<>(optionsData.include);
    }

}
