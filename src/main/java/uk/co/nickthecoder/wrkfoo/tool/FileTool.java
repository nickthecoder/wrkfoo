package uk.co.nickthecoder.wrkfoo.tool;

import java.util.Date;

import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class FileTool extends AbstractListTool<FileTask, RelativePath>
{
    public FileTool( FileTask task )
    {
        super(task);
    }

    @Override
    protected Columns<RelativePath> createColumns()
    {
        Columns<RelativePath> columns = new Columns<>();

        columns.add(new Column<RelativePath>(String.class, "path")
        {
            @Override
            public String getValue(RelativePath row)
            {
                return row.path;
            }

        }.tooltip(1).width(200));

        columns.add(new Column<RelativePath>(Date.class, "lastModified")
        {
            @Override
            public Date getValue(RelativePath row)
            {
                return new Date(row.getFile().lastModified());
            }
        }.width(120).lock().renderer(DateRenderer.instance));


        columns.add(new Column<RelativePath>(Long.class, "size")
        {
            @Override
            public Long getValue(RelativePath row)
            {
                return row.getFile().length();
            }
        }.reverseSort().width(120).lock().renderer(SizeRenderer.getInstance()));

        return columns;
    }

}
