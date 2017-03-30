package uk.co.nickthecoder.wrkfoo.tool;

import uk.co.nickthecoder.wrkfoo.AbstractTextTool;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;

/**
 * A tool which does nothing!
 */
public class NullTool extends AbstractTextTool<ResultsPanel,NullTask>
{
    public NullTool()
    {
        super(new NullTask());
    }
}
