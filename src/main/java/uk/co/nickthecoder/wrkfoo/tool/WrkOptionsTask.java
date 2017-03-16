package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.BooleanParameter;
import uk.co.nickthecoder.jguifier.parameter.ChoiceParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.option.OptionsData.OptionData;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptions.OptionRow;

public class WrkOptionsTask extends Task implements ListResults<OptionRow>
{
    private List<OptionRow> results;

    public ChoiceParameter<URL> path = Resources.getInstance().createOptionsPathChoice(true);

    public StringParameter optionsName = new StringParameter.Builder("optionsName")
        .parameter();

    public BooleanParameter showIncludes = new BooleanParameter.Builder("showIncludes")
        .value(true).parameter();

    // public BooleanParameter showGlobals = new BooleanParameter.Builder("showGlobals")
    // .value(false).parameter();

    public WrkOptionsTask()
    {
        addParameters(path, optionsName, showIncludes); // , showGlobals);
    }

    public WrkOptionsTask(String name)
    {
        this();
        optionsName.setDefaultValue(name);
    }

    public WrkOptionsTask(File file)
    {
        this();
        optionsName.setDefaultValue(Util.removeExtension(file));
        try {
            path.setDefaultValue(file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<OptionRow> getResults()
    {
        return results;
    }

    @Override
    public void body()
    {
        results = new ArrayList<>();
        addedNames = new HashSet<>();

        add(optionsName.getValue());

        // TODO Once resources only has a single globals url, then finish this.
        // if (showGlobals.getValue()) {
        // }
    }

    private Set<String> addedNames;

    private void add(String name)
    {
        if (addedNames.contains(name)) {
            return;
        }
        addedNames.add(name);

        List<OptionsData> list;
        if (path.getValue() == null) {
            list = Resources.getInstance().readOptionsData(name);
        } else {
            list = new ArrayList<>(1);
            try {
                list.add(Resources.getInstance().readOptionsData(path.getValue(), name));
            } catch (URISyntaxException | IOException e) {
                // Do nothing. This file may not exist, which is ok.
            }
        }

        for (OptionsData item : list) {
            add(item);
        }
    }

    private void add(OptionsData optionsData)
    {
        for (OptionData data : optionsData.optionData) {
            results.add(new OptionRow(optionsData, data, optionsData.url));
        }

        if (showIncludes.getValue()) {

            for (String include : optionsData.include) {
                add(include);
            }
        }
    }

}
