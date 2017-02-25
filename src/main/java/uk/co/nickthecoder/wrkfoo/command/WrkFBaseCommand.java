package uk.co.nickthecoder.wrkfoo.command;

import java.awt.Color;
import java.io.File;

import javax.swing.Icon;
import javax.swing.UIManager;

import uk.co.nickthecoder.wrkfoo.CommandPanel;
import uk.co.nickthecoder.wrkfoo.TaskCommand;

public class WrkFBaseCommand extends TaskCommand<WrkFTask, File>
{
    public static final Color directoryColor = new Color( 255,255,230 );
    
    public static final Icon directoryIcon = UIManager.getIcon("FileView.directoryIcon");
    public static final Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

    public WrkFBaseCommand(WrkFTask task)
    {
        super(task);
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
    }

}
