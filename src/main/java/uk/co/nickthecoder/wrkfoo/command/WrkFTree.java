package uk.co.nickthecoder.wrkfoo.command;

public class WrkFTree extends WrkFBaseCommand
{
    public static WrkFTask createTask()
    {
        WrkFTask task = new WrkFTask();

        task.depth.setValue(1);

        task.includeDirectories.setValue(true);

        return task;
    }

    public WrkFTree()
    {
        super(createTask());
        
        //getColumns().find("path").visible = true;
        //getColumns().find("name").visible = false;
    }
    

    @Override
    public String getLongTitle()
    {
        return "WrkFTree " + getTitle();
    }
}
