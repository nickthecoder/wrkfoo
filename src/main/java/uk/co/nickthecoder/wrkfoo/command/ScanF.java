package uk.co.nickthecoder.wrkfoo.command;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.ListCommand;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.command.ScanFTask.ScannedDirectory;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class ScanF extends ListCommand<ScanFTask, ScannedDirectory>
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

        columns.add(new Column<ScannedDirectory>(String.class, "mount point")
        {
            @Override
            public String getValue(ScannedDirectory row)
            {
                return row.path;
            }

        }.width(200));

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
    protected String optionsName()
    {
        return "scanf";
    }

    @Override
    public Icon getIcon()
    {
        return icon;
    }
}
