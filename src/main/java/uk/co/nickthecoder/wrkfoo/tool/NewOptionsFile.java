package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.io.FileNotFoundException;

import uk.co.nickthecoder.jguifier.StringParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;

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
        File file = Resources.instance.getOptionsFile(name.getValue());
        OptionsData od = new OptionsData(file);
        try {
            od.save();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
