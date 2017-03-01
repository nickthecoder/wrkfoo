package uk.co.nickthecoder.wrkfoo.command;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.ListCommand;
import uk.co.nickthecoder.wrkfoo.MainWindow;

public class WrkCommand extends ListCommand<WrkCommandTask, Command<?>>
{
    public static Icon icon;

    {
        try {
            icon = new ImageIcon(ImageIO.read(MainWindow.class.getResource("home.png")));
        } catch (Exception e) {
            // Do nothing
        }
    }

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
    protected String optionsName()
    {
        return "wrkcommand";
    }
}
