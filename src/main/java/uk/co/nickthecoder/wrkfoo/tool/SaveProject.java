package uk.co.nickthecoder.wrkfoo.tool;

import java.io.FileNotFoundException;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.Project;
import uk.co.nickthecoder.wrkfoo.Resources;

public class SaveProject extends Task
{
    public MainWindow mainWindow;

    public FileParameter saveAs = new FileParameter.Builder("saveAs")
        .writable().mayExist().file()
        .description("Save location")
        .parameter();

    public StringParameter description = new StringParameter.Builder("description")
        .description("And optional description, which will appear in the window title")
        .optional().parameter();

    public SaveProject(MainWindow mainWindow)
    {
        this.mainWindow = mainWindow;
        addParameters(saveAs, description);

        if (mainWindow.projectFile == null) {
            saveAs.setDefaultValue(Resources.getInstance().getProjectsDirectory());
        } else {
            saveAs.setDefaultValue(mainWindow.projectFile);
        }
        description.setDefaultValue(mainWindow.description);
    }

    @Override
    public void body()
    {
        mainWindow.description = description.getValue();

        try {
            Project.save(saveAs.getValue(), mainWindow);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
