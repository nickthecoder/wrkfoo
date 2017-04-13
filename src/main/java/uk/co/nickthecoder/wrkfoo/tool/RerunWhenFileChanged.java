package uk.co.nickthecoder.wrkfoo.tool;

import java.nio.file.Path;

import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.Tool;

public class RerunWhenFileChanged extends WatchFileParameter
{
    private Tool<?> tool;

    public RerunWhenFileChanged(Tool<?> tool, FileParameter fileParameter)
    {
        super(fileParameter);
        this.tool = tool;
    }

    @Override
    public void fileChanged(Path path)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                Util.assertIsEDT();
                if (!tool.getTask().isRunning()) {
                    tool.go();
                }
            }
        });
    }
}
