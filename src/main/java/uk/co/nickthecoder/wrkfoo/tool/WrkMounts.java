package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.DragFileConverter;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.SimpleListTool;
import uk.co.nickthecoder.wrkfoo.tool.WrkMountsTask.MountPoint;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class WrkMounts extends SimpleListTool<WrkMountsTask, MountPoint>
{
    public static final Icon icon = Resources.icon("disks.png");

    public WrkMounts()
    {
        super(new WrkMountsTask());
        dragListConverter = new DragFileConverter<MountPoint>();
    }

    @Override
    protected Columns<MountPoint> createColumns()
    {
        Columns<MountPoint> columns = new Columns<>();

        columns.add(new Column<MountPoint>(File.class, "mount point")
        {
            @Override
            public File getValue(MountPoint row)
            {
                return row.file;
            }

        }.sort().width(200));

        columns.add(new Column<MountPoint>(String.class, "name")
        {
            @Override
            public String getValue(MountPoint row)
            {
                return row.store.name();
            }

        }.width(200));

        columns.add(new Column<MountPoint>(Long.class, "size")
        {
            @Override
            public Long getValue(MountPoint row)
            {
                return row.size;
            }
        }.width(120).minWidth(80).renderer(SizeRenderer.getInstance()));

        columns.add(new Column<MountPoint>(Float.class, "used %")
        {
            @Override
            public Float getValue(MountPoint row)
            {
                return 100.0f * row.used / row.size;
            }
        }.width(120).minWidth(80));

        columns.add(new Column<MountPoint>(Float.class, "available %")
        {
            @Override
            public Float getValue(MountPoint row)
            {
                return 100.0f * row.available / row.size;
            }
        }.width(120).minWidth(80));

        return columns;
    }

    @Override
    public Icon getIcon()
    {
        return icon;
    }

}
