package uk.co.nickthecoder.wrkfoo.command;

public class WrkFTree extends WrkFBaseCommand
{
    public static WrkFTask createTask()
    {
        WrkFTask task = new WrkFTask();

        task.depth.setValue(20);

        return task;
    }

    public WrkFTree()
    {
        super(createTask());
        
        getColumns().find("path").visible = true;
        getColumns().find("name").visible = false;
    }
}
