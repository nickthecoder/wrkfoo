package uk.co.nickthecoder.wrkfoo.tool;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.ChoiceParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;

public class WrkOptionsIncludesTask extends Task implements ListResults<String>
{
    private List<String> results;

    public OptionsData optionsData;

    public ChoiceParameter<URL> path = Resources.getInstance().createOptionsPathChoice(false);

    public StringParameter optionsName = new StringParameter.Builder("optionsName")
        .parameter();

    public WrkOptionsIncludesTask(URL path, String optionsName)
    {
        addParameters(this.path, this.optionsName);

        this.path.setDefaultValue(path);
        this.optionsName.setDefaultValue(optionsName);
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

        OptionsData optionsData;
        try {
            optionsData = Resources.getInstance().readOptionsData(path.getValue(), optionsName.getValue());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        for (String name : optionsData.include) {
            names.add(name);
        }

        results = new ArrayList<>(names);
    }

}
