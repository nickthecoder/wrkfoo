package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.DirectoryTool;
import uk.co.nickthecoder.wrkfoo.DragFileConverter;
import uk.co.nickthecoder.wrkfoo.SimpleListTool;
import uk.co.nickthecoder.wrkfoo.TableResults;
import uk.co.nickthecoder.wrkfoo.tool.DiskUsageTask.ScannedDirectory;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class DiskUsage extends SimpleListTool<DiskUsageTask, ScannedDirectory>
    implements DirectoryTool<TableResults<ScannedDirectory>>
{
    public DiskUsage()
    {
        super(new DiskUsageTask());
        dragListConverter = new DragFileConverter<ScannedDirectory>();
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
                return row.file.getPath();
            }

        }).tooltip().width(200);

        columns.add(new Column<ScannedDirectory>(Long.class, "size")
        {
            @Override
            public Long getValue(ScannedDirectory row)
            {
                return row.size;
            }
        }).tooltip().reverseSort().width(120).lock().renderer(SizeRenderer.getInstance());

        return columns;
    }

    @Override
    public File getDirectory()
    {
        return task.directory.getValue();
    }
}
