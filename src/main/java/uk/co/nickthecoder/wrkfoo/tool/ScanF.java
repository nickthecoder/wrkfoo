package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.DirectoryTool;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.tool.ScanFTask.ScannedDirectory;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class ScanF extends AbstractListTool<ScanFTask, ScannedDirectory> implements DirectoryTool
{
    public static final Icon icon = Resources.icon("diskUsage.png");

    public ScanF()
    {
        super(new ScanFTask());
    }

    @Override
    protected Columns<ScannedDirectory> createColumns()
    {
        Columns<ScannedDirectory> columns = new Columns<ScannedDirectory>();

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
        }.reverseSort().width(120).minWidth(80).renderer(SizeRenderer.getInstance()));

        return columns;
    }

    @Override
    public Icon getIcon()
    {
        return icon;
    }

    @Override
    public File getDirectory()
    {
        return task.directory.getValue();
    }
}
