package uk.co.nickthecoder.wrkfoo.tool;

import java.io.FileNotFoundException;

import uk.co.nickthecoder.jguifier.StringChoiceParameter;
import uk.co.nickthecoder.jguifier.StringParameter;
import uk.co.nickthecoder.jguifier.Task;
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

    public StringParameter ifScript = new StringParameter.Builder("if")
        .multiLine().size(300, 80)
        .description("Groovy script. Is the row applicable to this option?")
        .optional().parameter();

    public EditOption(OptionsData optionsData, OptionData optionData)
    {
        this.optionData = optionData;
        this.optionsData = optionsData;

        code.setDefaultValue(optionData.code);
        label.setDefaultValue(optionData.label);
        action.setDefaultValue(optionData.action);

        if (optionData.multi) {
            type.setDefaultValue("multi");
        } else {
            type.setDefaultValue(optionData.isRow() ? "row" : "non-row");
        }
        ifScript.setDefaultValue(optionData.ifScript);

        addParameters(code, label, action, type, ifScript);
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

    }

    @Override
    public void post()
    {
        try {
            optionsData.save();
            optionsData.reload();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static class AddOption extends EditOption
    {

        public AddOption(OptionsData optionsData)
        {
            super(optionsData, new OptionData());
        }

        public void post()
        {
            optionsData.options.add(optionData);
            super.post();
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
            optionsData.options.remove(optionData);

            try {
                optionsData.save();
                optionsData.reload();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
