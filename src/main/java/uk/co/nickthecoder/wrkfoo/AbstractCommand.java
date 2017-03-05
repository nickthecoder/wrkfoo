package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.Parameter;
import uk.co.nickthecoder.jguifier.ParameterException;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.ValueParameter;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.wrkfoo.option.GroovyOption;
import uk.co.nickthecoder.wrkfoo.option.Options;
import uk.co.nickthecoder.wrkfoo.option.OptionsGroup;

public abstract class AbstractCommand<T extends Task, R> implements Command<R>
{
    protected Columns<R> columns;

    public T task;

    private CommandTab commandTab;

    private CommandPanel<R> commandPanel;

    private GoThread goThread;

    private List<CommandListener> commandListeners = new ArrayList<CommandListener>();

    public AbstractCommand(T task)
    {
        this.task = task;
    }

    @Override
    public void postCreate()
    {
        // Do nothing
    }

    @Override
    public T getTask()
    {
        return task;
    }

    @Override
    public String getTitle()
    {
        return task.getName();
    }

    @Override
    public String getShortTitle()
    {
        return getTitle();
    }

    @Override
    public String getLongTitle()
    {
        return getTitle();
    }

    @Override
    public String getName()
    {
        return this.getClass().getSimpleName();
    }

    @Override
    public Icon getIcon()
    {
        return null;
    }

    @Override
    public GroupParameter getParameters()
    {
        return task.getParameters();
    }

    public Columns<R> getColumns()
    {
        if (columns == null) {
            columns = createColumns();
        }
        return columns;
    }

    protected abstract Columns<R> createColumns();

    @Override
    public SimpleTable<R> createTable()
    {
        SimpleTable<R> result = getColumns().createTable(getTableModel());
        return result;
    }

    @Override
    public void attachTo(CommandTab tab)
    {
        assert (this.commandTab == null);

        this.commandTab = tab;
    }

    @Override
    public void detach()
    {
        this.commandTab = null;
        this.clearResults();
        this.commandPanel = null;
        this.columns = null;
    }

    @Override
    public CommandTab getCommandTab()
    {
        return commandTab;
    }

    @Override
    public ParametersPanel createParametersPanel()
    {
        ParametersPanel pp = new ParametersPanel();
        pp.addParameters(getTask().getParameters());
        return pp;

    }

    public CommandPanel<R> getCommandPanel()
    {
        if (commandPanel == null) {
            commandPanel = new CommandPanel<R>(this);
            commandPanel.postCreate();
        }

        return commandPanel;
    }

    public class GoThread extends Thread
    {
        @Override
        public void run()
        {
            try {
                if (commandTab != null) {
                    commandTab.go(AbstractCommand.this);
                } else {
                    task.run();
                }
                updateResults();
            } finally {
                end();
            }
        }
    }

    public void addCommandListener(CommandListener cl)
    {
        commandListeners.add(cl);
    }

    public void removeCommandListener(CommandListener cl)
    {
        commandListeners.remove(cl);
    }

    private void fireChangedState(boolean isRunning)
    {
        for (CommandListener cl : commandListeners) {
            try {
                cl.changedState(isRunning);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean isRunning()
    {
        return goThread != null;
    }

    /**
     * Routed through commandTab, so that it can record the history.
     */
    @Override
    public synchronized void go()
    {
        if (goThread == null) {
            goThread = new GoThread();

            fireChangedState(true);

            try {
                goThread.start();
            } catch (Exception e) {
                goThread = null;
            }
        }
    }

    @Override
    public synchronized void stop()
    {
        if (task instanceof Stoppable) {
            ((Stoppable) task).stop();
        }
    }

    private synchronized void end()
    {
        goThread = null;
        fireChangedState(false);
    }

    public abstract void updateResults();

    private Options options;

    public File getOptionsFile()
    {
        return Resources.instance.getOptionsFile(optionsName());
    }

    public Options getOptions()
    {
        if (options == null) {

            OptionsGroup og = new OptionsGroup();
            String name = optionsName();
            if (name != null) {
                og.add(Resources.instance.readOptions(name));
            }
            og.add(Resources.instance.globalOptions());
            options = og;
        }
        return options;
    }

    protected String optionsName()
    {
        return null;
    }

    /**
     * A convenience method for {@link GroovyOption}s to change a parameter, and return the same Command.
     * 
     * @param name
     *            The name of the parameter
     * @param value
     *            The new value for the parameter
     * @return this
     */
    @SuppressWarnings({ "unchecked" })
    public AbstractCommand<T, R> parameter(String name, Object value)
    {
        Parameter p = getTask().findParameter(name);
        ValueParameter<Object> vp = (ValueParameter<Object>) p;

        vp.setValue(value);
        return this;
    }

    /**
     * Rather than trying to duplicate a command by cloning it, this creates a new instance of the same type of
     * command, and then copies the task's parameter values across.
     * In rare cases commands may need to override this method to perform additional logic.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AbstractCommand<T, R> duplicate()
    {
        try {
            AbstractCommand<T, R> copy = this.getClass().newInstance();

            for (ValueParameter src : getTask().getParameters().allValueParameters()) {
                ValueParameter dest = ((ValueParameter) copy.getTask().findParameter(src.getName()));
                try {
                    dest.setValue(src.getValue());
                } catch (ParameterException e) {
                    dest.setDefaultValue(src.getValue());
                }
            }
            return copy;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
