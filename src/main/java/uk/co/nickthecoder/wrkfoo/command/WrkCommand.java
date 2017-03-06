package uk.co.nickthecoder.wrkfoo.command;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.ListCommand;
import uk.co.nickthecoder.wrkfoo.Resources;

public class WrkCommand extends ListCommand<WrkCommandTask, Command<?>>
{
    public static Icon icon = Resources.icon("home.png");

    public WrkCommand()
    {
        super(new WrkCommandTask());
    }

    @Override
    public Icon getIcon()
    {
        return icon;
    }

    @Override
    public Columns<Command<?>> createColumns()
    {
        Columns<Command<?>> columns = new Columns<Command<?>>();

        columns.add(new Column<Command<?>>(Icon.class, "")
        {
            @Override
            public Icon getValue(Command<?> row)
            {
                return row.getIcon();
            }

        }.width(30).lock());
        
        columns.add(new Column<Command<?>>(String.class, "Name")
        {
            @Override
            public String getValue(Command<?> row)
            {
                return row.getName();
            }
        }.width(500).sort());


        return columns;
    }

    @Override
    protected String optionsName()
    {
        return "wrkcommand";
    }
}
