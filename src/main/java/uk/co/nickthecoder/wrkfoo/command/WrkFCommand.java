package uk.co.nickthecoder.wrkfoo.command;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.UIManager;

import uk.co.nickthecoder.wrkfoo.CommandPanel;
import uk.co.nickthecoder.wrkfoo.TaskCommand;

public class WrkFCommand extends TaskCommand<WrkFTask, File>
{
    public static final Icon directoryIcon = UIManager.getIcon("FileView.directoryIcon");
    public static final Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

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

    public WrkFCommand()
    {
        super(createWrkFTask());
    }
    
    @Override
    public Icon getIcon()
    {
        return directoryIcon;
    }

    public String getTitle()
    {
        try {
            return getTask().directory.getValue().getPath();
        } catch (Exception e) {
            return super.getTitle();
        }
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
