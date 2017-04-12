package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.dnd.DnDConstants;
import java.io.File;
import java.util.List;

import uk.co.nickthecoder.jguifier.guiutil.DropFileListener;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.DirectoryTool;
import uk.co.nickthecoder.wrkfoo.TopLevel;

public class FileCopier implements DropFileListener
{
    private DirectoryTool<?> directoryTool;

    public FileCopier(DirectoryTool<?> parentTool)
    {
        this.directoryTool = parentTool;
    }

    @Override
    public void droppedFiles(List<File> files, int action)
    {
        Command command;
        if (action == DnDConstants.ACTION_COPY) {
            command = new Command("cp", "-rv", "--");
        } else if (action == DnDConstants.ACTION_MOVE) {
            command = new Command("mv", "-v", "--");
        } else {
            return;
        }

        for (File file : files) {
            command.addArg(file.getPath());
        }
        command.addArg(directoryTool.getDirectory().getPath());

        Terminal terminal = new Terminal(command);
        
        TopLevel.getTopLevel(directoryTool.getToolPanel().getComponent()).addTab(terminal);
    }

}
