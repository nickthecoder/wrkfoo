package uk.co.nickthecoder.wrkfoo;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.ParameterException;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.ValueParameter;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.GroovyOption;
import uk.co.nickthecoder.wrkfoo.option.Options;
import uk.co.nickthecoder.wrkfoo.option.OptionsGroup;

public abstract class AbstractTool<S extends ResultsPanel, T extends Task>
    implements Tool<S>
{
    public T task;

    private ToolTab toolTab;

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
        String title = Util.uncamel(getClass().getSimpleName());
        if (title.endsWith("Tool")) {
            return title.substring(0, title.length() - 4);
        }
        return title;
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

    public String getOptionsName()
    {
        String name = getClass().getSimpleName().toLowerCase();
        if (name.endsWith("tool")) {
            return name.substring(0, name.length() - 4);
        }
        if (name.startsWith("wrk")) {
            return name.substring(3);
        }
        return name;
    }

    @Override
    public Icon getIcon()
    {
        return Resources.icon(getOptionsName() + ".png");
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
        focusOnResults(importance);
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
    public AbstractTool<S,T> parameter(String name, Object value)
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
    public AbstractTool<S,T> duplicate()
    {
        try {
            AbstractTool<S,T> copy = this.getClass().newInstance();

            for (ValueParameter src : getTask().valueParameters()) {
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

    private S resultsPanel;
    
    protected abstract S createResultsPanel();

    @Override
    public S getResultsPanel()
    {
        if ( resultsPanel == null) {
            resultsPanel = createResultsPanel();
        }
        return resultsPanel;
    }

    @Override
    public ParametersPanel createParametersPanel()
    {
        ParametersPanel pp = new ParametersPanel();
        pp.addParameters(getTask().getRootParameter());
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
