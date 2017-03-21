package uk.co.nickthecoder.wrkfoo.tool;

import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.TaskListener;
import uk.co.nickthecoder.jguifier.parameter.BooleanParameter;
import uk.co.nickthecoder.wrkfoo.AbstractTool;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;
import uk.co.nickthecoder.wrkfoo.util.ProcessListener;
import uk.co.nickthecoder.wrkfoo.util.ProcessPoller;

public class Terminal extends AbstractTool<TerminalTask> implements TaskListener, ProcessListener
{
    public BooleanParameter autoClose = new BooleanParameter.Builder("autoClose")
        .parameter();

    /**
     * Can we re-run this command? If it is a user-defined command, set using the TerminalTask's
     * parameters, then it can be re-run. However, if it was created from a Command object, then it cannot be
     * re-run.
     */
    private final boolean reRunnable;

    public Terminal()
    {
        super(new TerminalTask());
        init();
        reRunnable = true;
    }

    public Terminal(Command command)
    {
        super(new TerminalTask(command));
        init();
        reRunnable = false;
    }

    private final void init()
    {
        task.addParameter(autoClose);
        task.addTaskListener(this);
        System.out.println("Added task Terminal listener");
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
    @Override
    public void started(Task tsk)
    {
    }

    @Override
    public void ended(Task tsk, boolean normally)
    {
        ProcessPoller pp = task.getProcessPoller();
        System.out.println("listening to the process poller");
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                // TODO Bodge?
                task.panel.doLayout();
                task.panel.repaint();
            }
        });
        pp.addProcessListener(this);
        focus(5);
    }

    @Override
    public void aborted(Task tsk)
    {
    }

    @Override
    public void finished(Process process)
    {
        System.out.println("Terminal's process has finished");
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                if (autoClose.getValue()) {
                    getToolTab().getTabbedPane().removeTab(getToolTab());
                }
            }
        });
    }
}
