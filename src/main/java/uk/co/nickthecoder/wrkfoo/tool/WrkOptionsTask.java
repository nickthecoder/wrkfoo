package uk.co.nickthecoder.wrkfoo.tool;

import java.io.IOException;
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
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.option.OptionsData.OptionData;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptionsTask.OptionRow;

public class WrkOptionsTask extends Task implements ListResults<OptionRow>
{
    private List<OptionRow> results;

    public ChoiceParameter<URL> path = Resources.getInstance().createOptionsPathChoice(true);

    public StringParameter optionsName = new StringParameter.Builder("optionsName")
        .parameter();

    public BooleanParameter showIncludes = new BooleanParameter.Builder("showIncludes")
        .value(true).parameter();

    public BooleanParameter showReadOnlyOptions = new BooleanParameter.Builder("showReadOnlyOptions")
        .value(false).parameter();

    public WrkOptionsTask()
    {
        addParameters(path, optionsName, showIncludes, showReadOnlyOptions);
    }

    public WrkOptionsTask(String name)
    {
        this();
        optionsName.setDefaultValue(name);
    }

    public WrkOptionsTask(URL path, String name)
    {
        this();
        this.path.setDefaultValue(path);
        this.optionsName.setDefaultValue(name);
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

        add(optionsName.getValue(), false);
    }

    private Set<String> addedNames;

    private void add(String name, boolean included)
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
            add(item, included);
        }
    }

    private void add(OptionsData optionsData, boolean included )
    {
        for (OptionData data : optionsData.optionData) {
            OptionRow row = new OptionRow(optionsData, data, optionsData.url, included);
            if (showReadOnlyOptions.getValue() || row.canEdit()) {
                results.add(row);
            }
        }

        if (showIncludes.getValue()) {
            for (String include : optionsData.include) {
                add(include, true);
            }
        }
    }

    public static class OptionRow
    {
        public OptionsData options;
        public OptionsData.OptionData option;
        public URL url;
        public boolean included;

        public OptionRow(OptionsData optionsData, OptionsData.OptionData data, URL url, boolean included)
        {
            this.options = optionsData;
            this.option = data;
            this.url = url;
            this.included = included;
        }

        public boolean canEdit()
        {
            return url.getProtocol().equals("file");
        }
    }
}
