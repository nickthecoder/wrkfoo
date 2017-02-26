package uk.co.nickthecoder.wrkfoo.command;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import uk.co.nickthecoder.wrkfoo.MainWindow;


public class WrkFCommand extends WrkFBaseCommand
{
    public static WrkFTask createWrkFTask()
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

    public WrkFCommand()
    {
        super(createWrkFTask());
    }
    
    @Override
    public void postCreate()
    {
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
