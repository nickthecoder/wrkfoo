package uk.co.nickthecoder.wrkfoo.tool;

import java.io.FileNotFoundException;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.TabSetData;

public class SaveTabSet extends Task
{
    public MainWindow mainWindow;

    public FileParameter saveAs = new FileParameter.Builder("saveAs")
        .writable().mayExist().file()
        .description("Save location")
        .parameter();

    public StringParameter description = new StringParameter.Builder("description")
        .description("And optional description, which will appear in the window title")
        .optional().parameter();

    public SaveTabSet(MainWindow mainWindow)
    {
        this.mainWindow = mainWindow;
        addParameters(saveAs, description);

        if (mainWindow.tabSetFile == null) {
            saveAs.setDefaultValue(Resources.instance.getTabsDirectory());
        } else {
            saveAs.setDefaultValue(mainWindow.tabSetFile);
        }
        description.setDefaultValue(mainWindow.description);
    }

    @Override
    public void body()
    {
        mainWindow.description = description.getValue();

        try {
            TabSetData.save(saveAs.getValue(), mainWindow);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // TODO Report the error.
        }
    }

}
