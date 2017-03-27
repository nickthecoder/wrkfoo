package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.DirectoryTool;
import uk.co.nickthecoder.wrkfoo.tool.DiskUsageTask.ScannedDirectory;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class DiskUsage extends AbstractListTool<DiskUsageTask, ScannedDirectory> implements DirectoryTool
{
    public DiskUsage()
    {
        super(new DiskUsageTask());
    }

    @Override
    protected Columns<ScannedDirectory> createColumns()
    {
        Columns<ScannedDirectory> columns = new Columns<>();

        columns.add(new Column<ScannedDirectory>(String.class, "path")
        {
            @Override
            public String getValue(ScannedDirectory row)
            {
                return row.path;
            }

        }.tooltip(1).width(200));

        columns.add(new Column<ScannedDirectory>(Long.class, "size")
        {
            @Override
            public Long getValue(ScannedDirectory row)
            {
                return row.size;
            }
        }.reverseSort().width(120).lock().renderer(SizeRenderer.getInstance()));

        return columns;
    }

    @Override
    public File getDirectory()
    {
        return task.directory.getValue();
    }
}
