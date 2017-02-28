package uk.co.nickthecoder.wrkfoo.command;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import uk.co.nickthecoder.wrkfoo.MainWindow;



public class WrkF extends WrkFBaseCommand
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
    
    @Override
    public void postCreate()
    {
        super.postCreate();
        
        MainWindow.putAction("alt UP", "upDirectory", getCommandPanel(), new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                File parent = getTask().directory.getValue().getParentFile();
                if (parent != null) {
                    getTask().directory.setValue(parent);
                }
                go();
            }
        });
    }
}
