package uk.co.nickthecoder.wrkfoo.tool;

import uk.co.nickthecoder.wrkfoo.AbstractTextTool;

/**
 * A tool which does nothing!
 */
public class NullTool extends AbstractTextTool<NullTask>
{
    public NullTool()
    {
        super(new NullTask());
    }
}
