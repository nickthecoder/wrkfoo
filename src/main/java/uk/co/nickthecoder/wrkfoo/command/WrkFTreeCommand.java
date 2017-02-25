package uk.co.nickthecoder.wrkfoo.command;

public class WrkFTreeCommand extends WrkFBaseCommand
{
    public static WrkFTask createWrkFTask()
    {
        WrkFTask task = new WrkFTask();

        task.depth.setValue(20);

        task.getColumns().find("path").visible = true;
        task.getColumns().find("name").visible = false;
        return task;
    }

    public WrkFTreeCommand()
    {
        super(createWrkFTask());
    }
}
