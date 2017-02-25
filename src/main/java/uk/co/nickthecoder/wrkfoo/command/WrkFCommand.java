package uk.co.nickthecoder.wrkfoo.command;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import uk.co.nickthecoder.wrkfoo.CommandPanel;


public class WrkFCommand extends WrkFBaseCommand
{
    public static WrkFTask createWrkFTask()
    {
        WrkFTask task = new WrkFTask();

        task.sort.hide = true;
        task.reverse.hide = true;
        task.order.hide = true;
        task.canonical.hide = true;
        task.depth.hide = true;
        task.enterHidden.hide = true;

        task.includeDirectories.setValue(true);

        return task;
    }

    public WrkFCommand()
    {
        super(createWrkFTask());
    }
    
    @Override
    public void postCreate(CommandPanel<File> cp)
    {
        cp.putAction("alt UP", "upDirectory", new AbstractAction()
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
