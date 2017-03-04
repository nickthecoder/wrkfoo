package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.io.FileNotFoundException;

import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.StringParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.TabSetData;

public class SaveTabSet extends Task
{
    public MainWindow mainWindow;

    public FileParameter fileParameter = new FileParameter.Builder("file")
        .writable().mayExist().file()
        .value(new File(Resources.instance.getTabsDirectory(), "new.json")).parameter();

    public StringParameter description = new StringParameter.Builder("description")
        .optional().parameter();

    public SaveTabSet(MainWindow mainWindow)
    {
        this.mainWindow = mainWindow;
        addParameters(fileParameter, description);

        fileParameter.setDefaultValue(mainWindow.tabSetFile);
        description.setDefaultValue(mainWindow.description);
    }

    @Override
    public void body()
    {
        mainWindow.description = description.getValue();

        try {
            TabSetData.save(fileParameter.getValue(), mainWindow);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // TODO Report the error.
        }
    }

}
