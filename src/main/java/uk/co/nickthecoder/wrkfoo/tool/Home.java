package uk.co.nickthecoder.wrkfoo.tool;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Tool;

public class Home extends AbstractListTool<HomeTask, Tool>
{
    public Home()
    {
        super(new HomeTask());
    }

    @Override
    public Columns<Tool> createColumns()
    {
        Columns<Tool> columns = new Columns<>();

        columns.add(new Column<Tool>(Icon.class, "")
        {
            @Override
            public Icon getValue(Tool row)
            {
                return row.getIcon();
            }

        }.width(30).lock());

        columns.add(new Column<Tool>(String.class, "Name")
        {
            @Override
            public String getValue(Tool row)
            {
                return row.getLongTitle();
            }
        }.width(500).sort());

        return columns;
    }
}
