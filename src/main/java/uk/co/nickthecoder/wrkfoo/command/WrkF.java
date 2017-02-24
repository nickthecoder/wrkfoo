package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.wrkfoo.CommandPanel;
import uk.co.nickthecoder.wrkfoo.TaskCommand;

public class WrkF extends TaskCommand<WrkFTask, File>
{
    public WrkF()
    {
        super(new WrkFTask(), "name", "lastModified", "size");
    }

    @Override
    public void defaultAction(File file)
    {
        System.out.println("Default action on " + file);
        if (file.isDirectory()) {
            System.out.println("Entering directory");
            getTask().directory.setValue(file);
            go();
            System.out.println("Ran");
        }
    }
}
