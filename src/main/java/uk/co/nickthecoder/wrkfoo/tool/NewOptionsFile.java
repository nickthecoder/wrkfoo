package uk.co.nickthecoder.wrkfoo.tool;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;

public class NewOptionsFile extends Task
{
    public StringParameter name = new StringParameter.Builder("name")
        .parameter();

    public NewOptionsFile()
    {
        addParameters(name);
    }

    @Override
    public void body()
    {
        // TODO Cannot create a new optionData file, because we don't know which path to put it in.
        /*
        File file = Resources.getInstance().getOptionsFile(name.getValue());
        OptionsData od = new OptionsData(file.toURI());
        try {
            od.save();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        */
    }

}
