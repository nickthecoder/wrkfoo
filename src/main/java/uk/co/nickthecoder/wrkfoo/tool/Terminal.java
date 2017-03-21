package uk.co.nickthecoder.wrkfoo.tool;

import uk.co.nickthecoder.wrkfoo.AbstractTool;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;

public class Terminal extends AbstractTool<TerminalTask>
{
    /**
     * Can we re-run this command? If it is a user-defined command, set using the TerminalTask's
     * parameters, then it can be re-run. However, if it was created from a Command object, then it cannot be
     * re-run.
     */
    private final boolean reRunnable;

    public Terminal()
    {
        super(new TerminalTask());
        reRunnable = true;
    }

    public Terminal(Command command)
    {
        super(new TerminalTask(command));
        reRunnable = false;
    }

    public boolean isRerunnable()
    {
        return reRunnable;
    }

    @Override
    public void updateResults()
    {
    }

    @Override
    public ResultsPanel createResultsComponent()
    {
        return task.createResultsComponent();
    }


    @Override
    public void detach()
    {
        super.detach();
        task.detach();
    }
}
