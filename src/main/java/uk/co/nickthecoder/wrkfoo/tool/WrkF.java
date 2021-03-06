package uk.co.nickthecoder.wrkfoo.tool;

public class WrkF extends WrkFBase
{

    public WrkF()
    {
        super();

        task.sort.visible = false;
        task.reverse.visible = false;
        task.order.visible = false;
        task.canonical.visible = false;
        task.depth.visible = false;
        task.enterHidden.visible = false;

        task.includeDirectories.setValue(true);
    }

    @Override
    public String getLongTitle()
    {
        return "WrkF " + getTitle();
    }
}
