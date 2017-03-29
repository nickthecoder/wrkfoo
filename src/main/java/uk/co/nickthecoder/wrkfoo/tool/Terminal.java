package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.TaskListener;
import uk.co.nickthecoder.jguifier.parameter.BooleanParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.AbstractUnthreadedTool;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;
import uk.co.nickthecoder.wrkfoo.ToolTab;
import uk.co.nickthecoder.wrkfoo.util.ProcessListener;
import uk.co.nickthecoder.wrkfoo.util.ProcessPoller;

public class Terminal extends AbstractUnthreadedTool<TerminalTask> implements TaskListener, ProcessListener
{
    public static Icon icon = Resources.icon("terminal.png");

    public StringParameter title = new StringParameter.Builder("title")
        .value("Terminal")
        .parameter();

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
        task.insertParameter(2, title);
        init();
        reRunnable = true;
    }

    public Terminal(Command command)
    {
        super(new TerminalTask(command));
        task.insertParameter(0, title);
        init();
        reRunnable = false;
    }

    private final void init()
    {
        task.addParameters(autoClose, killOnClose);
        task.addTaskListener(this);
    }

    /**
     * A Fluent API for setting the directory
     * 
     * @param directory
     * @return this
     */
    public Terminal dir(File directory)
    {
        task.directory.setValue(directory);
        return this;
    }

    /**
     * A Fluent API for setting the directory
     * 
     * @param path
     *            The directory's path
     * @return this
     */
    public Terminal dir(String path)
    {
        task.directory.setValue(new File(path));
        return this;
    }

    /**
     * A fluent API for choosing to use the simple terminal (instead of JediTermWidget).
     * 
     * @return this
     */
    public Terminal simple()
    {
        task.useSimpleTerminal.setValue(true);
        return this;
    }

    /**
     * A fluent API for setting the name, as it will appear in the tab.
     * 
     * @param value
     * @return this
     */
    public Terminal title(String value)
    {
        title.setValue(value);
        return this;
    }

    public Icon getIcon()
    {
        return icon;
    }

    @Override
    public String getTitle()
    {
        return title.getValue();
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
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                ToolTab tab = getToolTab();
                if (tab == null) {
                    return;
                }

                if (tab.getTabbedPane().getSelectedTab() == tab) {
                    MainWindow.focusLater("Terminal process finished",
                        MainWindow.getMainWindow(tab.getPanel()).getOptionField(), 5);
                }

                if (autoClose.getValue()) {
                    tab.getTabbedPane().removeTab(getToolTab());
                }
            }
        });
    }

    @Override
    public void focusOnResults(int importance)
    {
        task.focusOnResults(importance);
    }
}
