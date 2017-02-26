package uk.co.nickthecoder.wrkfoo.command;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import uk.co.nickthecoder.jguifier.util.FileListerTask;
import uk.co.nickthecoder.wrkfoo.MainWindow;


public class WrkFCommand extends WrkFBaseCommand
{
    public static FileListerTask createTask()
    {
        FileListerTask task = new FileListerTask();

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
        super(createTask());
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
