package uk.co.nickthecoder.wrkfoo.tool;

import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.AbstractListTool;

/**
 * A tool which does nothing!
 */
public class NullTool extends AbstractListTool<NullTask, Object>
{
    public NullTool()
    {
        super(new NullTask());
    }

    @Override
    protected Columns<Object> createColumns()
    {
        return new Columns<Object>();
    }

}
