package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.util.Date;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.util.FileListerTask;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Results;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class WrkFTask extends FileListerTask implements Results<File>
{
    private Columns<File> columns;

    /**
     * Amount of characters to chop off of the path column.
     */
    private int chopPath = 0;

    public WrkFTask()
    {
        super();
        setName("WrkFCommand");
    }

    @Override
    public Columns<File> getColumns()
    {
        if (columns == null) {
            columns = new Columns<File>();

            columns.add(new Column<File>(Icon.class, "")
            {
                @Override
                public Icon getValue(File row)
                {
                    return row.isDirectory() ? WrkFCommand.directoryIcon : WrkFCommand.fileIcon;
                }
            }.width(25).lock());

            columns.add(new Column<File>(String.class, "path")
            {
                @Override
                public String getValue(File row)
                {
                    return row.getPath().substring(chopPath);
                }
            }.hide().width(500));

            columns.add(new Column<File>(String.class, "name")
            {
                @Override
                public String getValue(File row)
                {
                    return row.getName();
                }
            }.width(300));

            columns.add(new Column<File>(Date.class, "lastModified")
            {
                @Override
                public Date getValue(File row)
                {
                    return new Date(row.lastModified());
                }
            }.width(120).lock().renderer(DateRenderer.instance));

            columns.add(new Column<File>(Long.class, "size")
            {
                @Override
                public Long getValue(File row)
                {
                    return row.length();
                }
            }.width(120).minWidth(80).renderer(SizeRenderer.getInstance()));

        }
        return columns;
    }

    @Override
    public void post()
    {
        super.post();
        chopPath = directory.getValue().getPath().length() + 1;
    }

    @Override
    public File getRow(int row)
    {
        return results.get(row);
    }

    @Override
    public void clearResults()
    {
        results.clear();
    }
}
