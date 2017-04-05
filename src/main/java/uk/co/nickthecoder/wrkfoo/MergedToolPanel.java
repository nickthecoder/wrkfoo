package uk.co.nickthecoder.wrkfoo;

import javax.swing.JComponent;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptions;
import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

/**
 * Used instead of a {@link RealToolPanel} when a {@link Tool} does not have its own panel.
 * For an example of its use, see {@link WrkOptions} - The main WrkOptions tool has a hidden inner
 * WrkOptionsIncludes tool, which has the FakeToolPanel.
 */
public class MergedToolPanel implements ToolPanel
{
    private ToolPanel other;
    
    public MergedToolPanel( ToolPanel other )
    {
        this.other = other;
    }

    public JComponent getComponent()
    {
        return null;
    }
    
    @Override
    public ToolPanelToolBar getToolBar()
    {
        return other.getToolBar();
    }

    @Override
    public void postCreate()
    {
    }

    @Override
    public HidingSplitPane getSplitPane()
    {
        return null;
    }

    @Override
    public ParametersPanel getParametersPanel()
    {
        return null;
    }

    @Override
    public boolean check()
    {
        return true;
    }

    @Override
    public void go()
    {
    }

    @Override
    public TopLevel getTopLevel()
    {
        return other.getTopLevel();
    }

    @Override
    public void attachTo(ToolTab tab)
    {
    }

    @Override
    public void detach()
    {        
    }

    @Override
    public ToolTab getToolTab()
    {
        return other.getToolTab();
    }

}
