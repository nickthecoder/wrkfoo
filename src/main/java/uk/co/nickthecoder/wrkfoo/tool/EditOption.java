package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.BooleanParameter;
import uk.co.nickthecoder.jguifier.parameter.ChoiceParameter;
import uk.co.nickthecoder.jguifier.parameter.StringChoiceParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.option.OptionsData.OptionData;

public class EditOption extends Task
{
    protected OptionsData optionsData;

    protected OptionData optionData;

    public StringParameter code = new StringParameter.Builder("code").columns(6).optional()
        .parameter();

    public StringParameter label = new StringParameter.Builder("label")
        .parameter();

    public StringParameter action = new StringParameter.Builder("action")
        .multiLine().size(300, 160)
        .parameter();

    public StringChoiceParameter type = new StringChoiceParameter.Builder("type")
        .choice("row", "row", "Row").choice("multi", "multi", "Multi-Row").choice("non-row", "non-row", "Non-Row")
        .parameter();

    public BooleanParameter newTab = new BooleanParameter.Builder("newTab")
        .parameter();

    public BooleanParameter refreshResults = new BooleanParameter.Builder("refreshResults")
        .parameter();

    public BooleanParameter prompt = new BooleanParameter.Builder("prompt")
        .description( "Should options be prompted, rather than running straight away")
        .parameter();

    public StringParameter ifScript = new StringParameter.Builder("if")
        .multiLine().size(300, 80)
        .description("Groovy script. Is the row applicable to this option?")
        .optional().parameter();

    public EditOption(OptionsData optionsData, OptionData optionData)
    {
        this.optionsData = optionsData;
        this.optionData = optionData;

        code.setDefaultValue(optionData.code);
        label.setDefaultValue(optionData.label);
        action.setDefaultValue(optionData.action);

        if (optionData.multi) {
            type.setDefaultValue("multi");
        } else {
            type.setDefaultValue(optionData.isRow() ? "row" : "non-row");
        }
        ifScript.setDefaultValue(optionData.ifScript);

        newTab.setDefaultValue(optionData.newTab);
        refreshResults.setDefaultValue(optionData.refreshResults);
        prompt.setDefaultValue(optionData.prompt);

        addParameters(code, label, action, type, newTab, refreshResults, prompt, ifScript);
    }

    @Override
    public void body()
    {
        optionData.code = code.getValue();
        optionData.label = label.getValue();
        optionData.action = action.getValue();
        optionData.row = !type.getValue().equals("non-row");
        optionData.multi = type.getValue().equals("multi");
        optionData.ifScript = ifScript.getValue();
        optionData.newTab = newTab.getValue();
        optionData.refreshResults = refreshResults.getValue();
        optionData.prompt = prompt.getValue();
    }

    @Override
    public void post()
    {
        optionsData.save();
        optionsData.reload();
    }

    public static class AddOption extends EditOption
    {
        public ChoiceParameter<File> path = Resources.getInstance().createOptionsDirectoryChoice();

        public StringParameter name = new StringParameter.Builder("name")
            .parameter();

        public AddOption(String optionsName)
        {
            super(null, new OptionData());
            name.setDefaultValue(optionsName);

            insertParameters(0, path, name);
        }

        @Override
        public void pre()
        {
            super.pre();
            try {
                optionsData = Resources.getInstance().readOptionsData(path.getValue(), name.getValue());
            } catch (URISyntaxException | IOException e) {
                // The file may not exist yet, in which case, we should try to create it.
                try {
                    optionsData = new OptionsData(path.getValue(), name.getValue());
                } catch (MalformedURLException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }

        @Override
        public void body()
        {
            super.body();
            optionsData.optionData.add(optionData);
        }

    }

    public static class DeleteOption extends Task
    {
        protected OptionsData optionsData;

        protected OptionData optionData;

        public DeleteOption(OptionsData optionsData, OptionData optionData)
        {
            this.optionData = optionData;
            this.optionsData = optionsData;
        }

        @Override
        public void body()
        {
            optionsData.optionData.remove(optionData);
            optionsData.save();
            optionsData.reload();
        }

    }
}
