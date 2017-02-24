package uk.co.nickthecoder.wrkfoo.command;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import uk.co.nickthecoder.wrkfoo.CommandPanel;
import uk.co.nickthecoder.wrkfoo.TaskCommand;

public class WrkF extends TaskCommand<WrkFTask, File>
{
    public static WrkFTask createWrkFTask()
    {
        WrkFTask task = new WrkFTask();
        
        task.sort.hide = true;
        task.reverse.hide = true;
        task.order.hide = true;
        task.depth.hide = true;
        task.enterHidden.hide = true;
        task.includeDirectories.setValue(true);
        
        return task;
    }
    
    public WrkF()
    {
        super(createWrkFTask(), "name", "lastModified", "size");
    }

    @Override
    public void defaultAction(File file)
    {
        if (file.isDirectory()) {
            getTask().directory.setValue(file);
            go();
        }
    }

    @Override
    public CommandPanel<File> createCommandPanel()
    {
        CommandPanel<File> cp = super.createCommandPanel();
        return cp;
    }
    
    @Override
    public void postCreate( CommandPanel<File> cp )
    {
        cp.putAction("ctrl UP", "upDirectory", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                File parent = getTask().directory.getValue().getParentFile();
                if (parent != null) {
                    getTask().directory.setValue( parent );
                }
                go();
            }
        });

    }
}
