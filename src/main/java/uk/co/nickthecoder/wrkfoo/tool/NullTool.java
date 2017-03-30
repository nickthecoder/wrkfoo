package uk.co.nickthecoder.wrkfoo.tool;

import uk.co.nickthecoder.wrkfoo.AbstractTool;
import uk.co.nickthecoder.wrkfoo.PanelResults;
import uk.co.nickthecoder.wrkfoo.Results;

/**
 * A tool which does nothing!
 */
public class NullTool extends AbstractTool<Results,NullTask>
{
    public NullTool()
    {
        super(new NullTask());
    }

    @Override
    public void updateResults()
    {       
    }

    @Override
    public void go()
    {
    }

    @Override
    public void stop()
    {
    }

    @Override
    protected Results createResultsPanel()
    {
        return new PanelResults();
    }
}
