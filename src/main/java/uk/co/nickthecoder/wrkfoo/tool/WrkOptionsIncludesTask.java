package uk.co.nickthecoder.wrkfoo.tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;

public class WrkOptionsIncludesTask extends Task implements ListResults<String>
{
    private List<String> results;

    public OptionsData optionsData;

    public StringParameter optionsName = new StringParameter.Builder("optionsName")
        .parameter();

    public WrkOptionsIncludesTask()
    {
        addParameters(optionsName);
    }

    @Override
    public List<String> getResults()
    {
        return results;
    }

    @Override
    public void body()
    {
        Set<String> names = new HashSet<>();

        List<OptionsData> list = Resources.getInstance().readOptionsData(optionsName.getValue());
        for (OptionsData od : list ) {
            for ( String name : od.include ) {
                names.add( name );
            }
        }
        
        results = new ArrayList<>(names);
    }

}
