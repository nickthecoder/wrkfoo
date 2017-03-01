package uk.co.nickthecoder.wrkfoo.command;

import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.ListCommand;

/**
 * A command which does nothing!
 */
public class NullCommand extends ListCommand<NullTask, Object>
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
