package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.ParameterException;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.ValueParameter;
import uk.co.nickthecoder.jguifier.parameter.GroupParameter;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.wrkfoo.option.GroovyOption;
import uk.co.nickthecoder.wrkfoo.option.Options;
import uk.co.nickthecoder.wrkfoo.option.OptionsGroup;
import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

public abstract class AbstractTool<T extends Task> implements Tool
{
    public T task;

    private ToolTab toolTab;

    private GoThread goThread;

    private List<ToolListener> toolListeners = new ArrayList<>();

    private ToolPanel toolPanel;

    private OptionsGroup options;

    public AbstractTool(T task)
    {
        this.task = task;
    }

    @Override
    public void postCreate()
    {
        // Do nothing
    }

    @Override
    public String getCreationString()
    {
        return getClass().getName();
    }

    @Override
    public T getTask()
    {
        return task;
    }

    /**
     * Used by both getShortTitle and getLongTitle unless either are overridden in sub-classes.
     * 
     * @return The title, which may be used
     */
    protected String getTitle()
    {
        return getClass().getSimpleName();
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

    public String getOptionsName()
    {
        return getClass().getSimpleName().toLowerCase();
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

    @Override
    public boolean isRerunnable()
    {
        return true;
    }

    @Override
    public void attachTo(ToolTab tab)
    {
        assert (this.toolTab == null);

        this.toolTab = tab;
    }

    @Override
    public void detach()
    {
        this.toolTab = null;
        this.toolPanel = null;
    }

    @Override
    public ToolTab getToolTab()
    {
        return toolTab;
    }

    public class GoThread extends Thread
    {
        @Override
        public void run()
        {
            try {
                task.run();
            } finally {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateResults();
                        end();
                    }
                });
            }
        }
    }

    @Override
    public void addToolListener(ToolListener cl)
    {
        toolListeners.add(cl);
    }

    @Override
    public void removeToolListener(ToolListener cl)
    {
        toolListeners.remove(cl);
    }

    private void fireChangedState()
    {
        for (ToolListener cl : toolListeners) {
            try {
                cl.changedState(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Routed through toolTab, so that it can record the history.
     */
    @Override
    public void go()
    {
        if (goThread == null) {
            goThread = new GoThread();

            fireChangedState();

            try {
                goThread.start();
            } catch (Exception e) {
                goThread = null;
            }
        }
    }

    @Override
    public void stop()
    {
        if (task instanceof Stoppable) {
            ((Stoppable) task).stop();
        }
    }

    private void end()
    {
        goThread = null;

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                getToolPanel().getSplitPane().showLeft();
                focusOnResults(7);
            }
        });
        fireChangedState();
    }

    /**
     * Focus on the results panel when the tool completes.
     * Called from {@link #end()}, and is here so that different tools can choose to focus where they see best.
     * Note. TableTools overrides this so that it can focus on {@link MainWindow}'s option text field when there are no
     * rows.
     */
    protected void focusOnResults(int importance)
    {
        MainWindow.focusLater("Results. Left.", getToolPanel().getSplitPane().getLeftComponent(), importance);
    }

    @Override
    public void focus(final int importance)
    {
        HidingSplitPane hsp = getToolPanel().getSplitPane();
        if (hsp.getRightComponent().isVisible()) {
            MainWindow.focusLater("Tool's parameters as they are showing", hsp.getRightComponent(), importance);
        } else {
            focusOnResults(importance);
        }
    }

    @Override
    public boolean isRunning()
    {
        return goThread != null;
    }

    /**
     * A convenience method for {@link GroovyOption}s to change a parameter, and return the same Tool.
     * 
     * @param name
     *            The name of the parameter
     * @param value
     *            The new value for the parameter
     * @return this
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AbstractTool<T> parameter(String name, Object value)
    {
        Parameter p = getTask().findParameter(name);
        ValueParameter vp = (ValueParameter) p;

        vp.setValue(value);
        return this;
    }

    /**
     * Rather than trying to duplicate a tool by cloning it, this creates a new instance of the same type of
     * tool, and then copies the task's parameter values across.
     * In rare cases tools may need to override this method to perform additional logic.
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AbstractTool<T> duplicate()
    {
        try {
            AbstractTool<T> copy = this.getClass().newInstance();

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

    @Override
    public ParametersPanel createParametersPanel()
    {
        ParametersPanel pp = new ParametersPanel();
        pp.addParameters(getTask().getParameters());
        return pp;
    }

    @Override
    public Options getOptions()
    {
        if (options == null) {
            options = new OptionsGroup();
            options.add(Resources.getInstance().readOptions(getOptionsName()));
            options.add(Resources.getInstance().globalOptions());
        }
        return options;
    }

    protected ToolPanel createToolPanel()
    {
        return new ToolPanel(this);
    }

    @Override
    public ToolPanel getToolPanel()
    {
        if (toolPanel == null) {
            toolPanel = createToolPanel();
            toolPanel.postCreate();
        }

        return toolPanel;
    }
}
