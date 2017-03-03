package uk.co.nickthecoder.wrkfoo.command;

public class WrkF extends WrkFBase
{
    public static WrkFTask createTask()
    {
        WrkFTask task = new WrkFTask();

        task.sort.visible = false;
        task.reverse.visible = false;
        task.order.visible = false;
        task.canonical.visible = false;
        task.depth.visible = false;
        task.enterHidden.visible = false;

        task.includeDirectories.setValue(true);

        return task;
    }

    public WrkF()
    {
        super(createTask());
    }

    @Override
    public String getLongTitle()
    {
        return "WrkF " + getTitle();
    }
}
