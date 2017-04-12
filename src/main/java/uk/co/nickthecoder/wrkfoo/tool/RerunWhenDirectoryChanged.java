package uk.co.nickthecoder.wrkfoo.tool;

import java.nio.file.Path;

import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.wrkfoo.Tool;

public class RerunWhenDirectoryChanged extends WatchDirectoryParameter
{
    private Tool<?> tool;

    public RerunWhenDirectoryChanged(Tool<?> tool, FileParameter directoryParameter)
    {
        super(directoryParameter);
        this.tool = tool;
    }

    @Override
    public void directoryChanged(Path directory, Path file)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                tool.go();
            }
        });
    }

}
