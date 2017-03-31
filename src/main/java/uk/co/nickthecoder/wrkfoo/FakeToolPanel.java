package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptions;
import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

/**
 * Used instead of a {@link RealToolPanel} when a {@link Tool} does not have its own panel.
 * For an example of its use, see {@link WrkOptions} - The main WrkOptions tool has a hidden inner
 * WrkOptionsIncludes tool, which has the FakeToolPanel.
 */
public class FakeToolPanel extends ToolPanel
{
    private static final long serialVersionUID = 1L;

    public FakeToolPanel()
    {
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
