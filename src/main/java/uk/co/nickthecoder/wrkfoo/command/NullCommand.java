package uk.co.nickthecoder.wrkfoo.command;

import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.AbstractListCommand;

/**
 * A command which does nothing!
 */
public class NullCommand extends AbstractListCommand<NullTask, Object>
{
    public NullCommand()
    {
        super(new NullTask());
    }

    @Override
    protected Columns<Object> createColumns()
    {
        return new Columns<Object>();
    }

}
