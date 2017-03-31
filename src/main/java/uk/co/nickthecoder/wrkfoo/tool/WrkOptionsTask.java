package uk.co.nickthecoder.wrkfoo.tool;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.ChoiceParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.option.OptionsData.OptionData;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptionsTask.OptionRow;

public class WrkOptionsTask extends Task implements ListResults<OptionRow>
{
    public ChoiceParameter<URL> path = Resources.getInstance().createOptionsPathChoice(false);

    public StringParameter optionsName = new StringParameter.Builder("optionsName")
        .parameter();

    private List<OptionRow> results;

    public WrkOptionsTask()
    {
        addParameters(path, optionsName);
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
    public void body() throws URISyntaxException
    {
        results = new ArrayList<>();

        try {
            OptionsData item = Resources.getInstance().readOptionsData(path.getValue(), optionsName.getValue());
            for (OptionData data : item.optionData) {
                OptionRow row = new OptionRow(item, data, item.url);
                results.add(row);
            }
        } catch (IOException e) {
            // Do nothing, we don't care if the file was not found
        }
    }

    public static class OptionRow
    {
        public OptionsData options;
        public OptionsData.OptionData option;
        public URL url;

        public OptionRow(OptionsData optionsData, OptionsData.OptionData data, URL url)
        {
            this.options = optionsData;
            this.option = data;
            this.url = url;
        }

        public boolean canEdit()
        {
            return url.getProtocol().equals("file");
        }
    }
}
