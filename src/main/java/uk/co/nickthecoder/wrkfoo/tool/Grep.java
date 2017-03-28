package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.DragFileConverter;
import uk.co.nickthecoder.wrkfoo.tool.GrepTask.GrepRow;

public class Grep extends GenericFileTool<GrepRow>
{
    public Grep(File directory)
    {
        super(new GrepTask(directory));
        dragListConverter = new DragFileConverter<GrepRow>();
    }

    public Grep()
    {
        super(new GrepTask());
        dragListConverter = new DragFileConverter<GrepRow>();
    }

    @Override
    protected void addExtraColumns(Columns<GrepRow> columns)
    {
        columns.add(new Column<GrepRow>(String.class, "text")
        {
            @Override
            public String getValue(GrepRow row)
            {
                return row.text;
            }
        }.tooltip(2).width(300));

        columns.add(new Column<GrepRow>(Integer.class, "line")
        {
            @Override
            public Integer getValue(GrepRow row)
            {
                return row.line;
            }
        }.width(60).lock());

    }

}
