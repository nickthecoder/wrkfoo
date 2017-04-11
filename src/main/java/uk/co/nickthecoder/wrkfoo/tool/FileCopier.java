package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.dnd.DnDConstants;
import java.io.File;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.TaskAdaptor;
import uk.co.nickthecoder.jguifier.guiutil.DropFileListener;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.Tool;
import uk.co.nickthecoder.wrkfoo.TopLevel;

public class FileCopier implements DropFileListener
{
    private File destinationDirectory;

    private Tool<?> tool;

    public FileCopier(Tool<?> parentTool)
    {
        this.tool = parentTool;
    }

    public void setDestination(File directory)
    {
        this.destinationDirectory = directory;
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
        command.addArg(destinationDirectory.getPath());

        Terminal terminal = new Terminal(command);
        terminal.getTask().addTaskListener(new TaskAdaptor()
        {
            @Override
            public void ended(Task task, boolean normally)
            {
                // Rerun the tool to update its results.
                tool.go();
            }
        });

        TopLevel.getTopLevel(tool.getToolPanel().getComponent()).addTab(terminal);
    }

}
