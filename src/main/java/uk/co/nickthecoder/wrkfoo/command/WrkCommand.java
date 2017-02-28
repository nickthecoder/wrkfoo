package uk.co.nickthecoder.wrkfoo.command;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Command;

public class WrkCommand extends ListCommand<WrkCommandTask, Command<?>>
{
    public WrkCommand()
    {
        super(new WrkCommandTask());
    }

    @Override
    public Columns<Command<?>> createColumns()
    {
        Columns<Command<?>> columns = new Columns<Command<?>>();

        columns.add(new Column<Command<?>>(String.class, "Name")
        {
            @Override
            public String getValue(Command<?> row)
            {
                return row.getName();
            }
        });

        return columns;
    }
    
    @Override
    public void defaultAction(Command<?> command)
    {
        getCommandTab().go(command);
    }

}
