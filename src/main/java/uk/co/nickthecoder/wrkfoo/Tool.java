package uk.co.nickthecoder.wrkfoo;

import javax.swing.Icon;

import groovyjarjarcommonscli.Option;
import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.GroupParameter;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.wrkfoo.option.Options;
import uk.co.nickthecoder.wrkfoo.tool.ExecTool;

/**
 * A Tool performs some work via a {@link Task}. The Task is defined as usual by a set of {@link Parameter}s.
 * The message of the task are a list of objects (of generic type R).
 * Typically these are held in a List, and use a {@link ListTableModel}.
 * 
 * Tools also have the meta-data needed to display these message in a GUI table,
 * i.e. it defines the columns, and any special cell renderers etc.
 * 
 */
public interface Tool
{
    public void postCreate();

    public Task getTask();

    public String getName();

    public String getShortTitle();

    public String getLongTitle();

    public Icon getIcon();

    public GroupParameter getParameters();

    public void attachTo(ToolTab tab);

    public void detach();

    public ToolTab getToolTab();

    /**
     * If this tool is created by an {@link Option}, should it cause a new tab to be created?
     * Useful for {@link ExecTool}s.
     */
    public boolean getUseNewTab();

    public void updateResults();

    public ResultsPanel createResultsComponent();

    public ParametersPanel createParametersPanel();

    public ToolPanel getToolPanel();

    public boolean isRerunnable();

    public void go();

    public void stop();

    public boolean isRunning();

    public Options getOptions();

    public Tool duplicate();

    public void addToolListener(ToolListener cl);

    public void removeToolListener(ToolListener cl);
}
