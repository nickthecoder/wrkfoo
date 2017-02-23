package uk.co.nickthecoder.wrkfoo.file;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import uk.co.nickthecoder.jguifier.util.FileListerTask;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.ColumnMap;
import uk.co.nickthecoder.wrkfoo.Results;

public class WrkFTask extends FileListerTask implements Results<File>
{
    protected static ColumnMap<File> columnMap;

    public WrkFTask()
    {
        super();
    }

    public Map<String, Column<File>> columnMap()
    {
        if (columnMap == null) {
            columnMap = new ColumnMap<File>();

            columnMap.add(new Column<File>("name")
            {
                @Override
                public Object getValue(File row)
                {
                    return row.getName();
                }
            });

            columnMap.add(new Column<File>("lastModified")
            {
                @Override
                public Object getValue(File row)
                {
                    return new Date(row.lastModified());
                }
            });

            columnMap.add(new Column<File>("size")
            {
                @Override
                public Object getValue(File row)
                {
                    return row.length();
                }
            });

        }
        return columnMap;
    }

    @Override
    public void processResults()
    {
        // TODO Split base class, so that this isn't needed. Do nothing
    }

    @Override
    public Iterator<File> rows()
    {
        return results.iterator();
    }
}
