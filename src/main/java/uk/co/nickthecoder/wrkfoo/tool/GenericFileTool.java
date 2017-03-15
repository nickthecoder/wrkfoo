package uk.co.nickthecoder.wrkfoo.tool;

import java.util.Date;

import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class GenericFileTool<R extends RelativePath> extends AbstractListTool<GenericFileTask<R>, R>
{
    public GenericFileTool(GenericFileTask<R> task)
    {
        super(task);
    }

    @Override
    protected Columns<R> createColumns()
    {
        Columns<R> columns = new Columns<>();

        columns.add(new Column<R>(String.class, "path")
        {
            @Override
            public String getValue(RelativePath row)
            {
                return row.path;
            }

        }.tooltip(1).width(200));

        addExtraColumns(columns);
        
        return columns;
    }

    protected void addExtraColumns(Columns<R> columns)
    {
        columns.add(new Column<R>(Date.class, "lastModified")
        {
            @Override
            public Date getValue(RelativePath row)
            {
                return new Date(row.getFile().lastModified());
            }
        }.width(120).lock().renderer(DateRenderer.instance));

        columns.add(new Column<R>(Long.class, "size")
        {
            @Override
            public Long getValue(R row)
            {
                return row.getFile().length();
            }
        }.reverseSort().width(120).lock().renderer(SizeRenderer.getInstance()));

    }

}
