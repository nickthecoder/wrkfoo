package uk.co.nickthecoder.wrkfoo.tool;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Tool;
import uk.co.nickthecoder.wrkfoo.Resources;

public class WrkTool extends AbstractListTool<WrkToolTask, Tool>
{
    public static Icon icon = Resources.icon("home.png");

    public WrkTool()
    {
        super(new WrkToolTask());
    }

    @Override
    public Icon getIcon()
    {
        return icon;
    }

    @Override
    public Columns<Tool> createColumns()
    {
        Columns<Tool> columns = new Columns<Tool>();

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
                return row.getName();
            }
        }.width(500).sort());


        return columns;
    }

    @Override
    protected String optionsName()
    {
        return "wrktool";
    }
}
