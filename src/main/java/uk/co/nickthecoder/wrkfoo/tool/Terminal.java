package uk.co.nickthecoder.wrkfoo.tool;

import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.TaskListener;
import uk.co.nickthecoder.jguifier.parameter.BooleanParameter;
import uk.co.nickthecoder.wrkfoo.AbstractUnthreadedTool;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;
import uk.co.nickthecoder.wrkfoo.ToolTab;
import uk.co.nickthecoder.wrkfoo.util.ProcessListener;
import uk.co.nickthecoder.wrkfoo.util.ProcessPoller;

public class Terminal extends AbstractUnthreadedTool<TerminalTask> implements TaskListener, ProcessListener
{
    public BooleanParameter autoClose = new BooleanParameter.Builder("autoClose")
        .parameter();

    public BooleanParameter killOnClose = new BooleanParameter.Builder("killOnClose")
        .value(true)
        .description("Kill the process when the tab is closed")
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
        task.addParameters(autoClose, killOnClose);
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
        if (killOnClose.getValue()) {
            task.killProcess();
        }
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
                ToolTab tab =  getToolTab();
                if (tab == null) {
                    return;
                }
                
                if (tab.getTabbedPane().getSelectedToolTab() == tab) {
                    MainWindow.focusLater("Terminal process finished",
                        MainWindow.getMainWindow(tab.getPanel()).getOptionField(), 5);
                }
                
                if (autoClose.getValue()) {
                    tab.getTabbedPane().removeTab(getToolTab());
                }
            }
        });
    }
}
