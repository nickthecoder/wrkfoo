package uk.co.nickthecoder.wrkfoo;

import javax.swing.JComponent;
import javax.swing.JPanel;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptions;
import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

/**
 * Used instead of a {@link RealToolPanel} when a {@link Tool} does not have its own panel.
 * For an example of its use, see {@link WrkOptions} - The main WrkOptions tool has a hidden inner
 * WrkOptionsIncludes tool, which has the FakeToolPanel.
 */
public abstract class FakeToolPanel implements ToolPanel
{
    private JPanel component;

    public FakeToolPanel()
    {
        component = new JPanel();
    }

    public JComponent getComponent()
    {
        return component;
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

}
