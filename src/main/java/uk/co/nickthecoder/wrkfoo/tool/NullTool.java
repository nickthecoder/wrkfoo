package uk.co.nickthecoder.wrkfoo.tool;

import uk.co.nickthecoder.wrkfoo.AbstractTool;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;

/**
 * A tool which does nothing!
 */
public class NullTool extends AbstractTool<ResultsPanel,NullTask>
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
    protected ResultsPanel createResultsPanel()
    {
        return new ResultsPanel();
    }
}
