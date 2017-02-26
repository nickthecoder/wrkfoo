package uk.co.nickthecoder.wrkfoo.command;

import uk.co.nickthecoder.jguifier.util.FileListerTask;

public class WrkFTreeCommand extends WrkFBaseCommand
{
    public static FileListerTask createTask()
    {
        FileListerTask task = new FileListerTask();

        task.depth.setValue(20);

        return task;
    }

    public WrkFTreeCommand()
    {
        super(createTask());
        
        getColumns().find("path").visible = true;
        getColumns().find("name").visible = false;
    }
}
