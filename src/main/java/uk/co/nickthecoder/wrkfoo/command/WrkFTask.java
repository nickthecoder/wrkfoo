package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JTable;

import uk.co.nickthecoder.jguifier.util.FileListerTask;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.ListTableModel;
import uk.co.nickthecoder.wrkfoo.Results;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class WrkFTask extends FileListerTask implements Results<File>
{
    private static Columns<File> columns;

    private ListTableModel<File> tableModel;

    public WrkFTask()
    {
        super();
    }

    @Override
    public Columns<File> getColumns()
    {
        if (columns == null) {
            columns = new Columns<File>();

            columns.add(new Column<File>(File.class, "name")
            {
                @Override
                public Object getValue(File row)
                {
                    return row.getName();
                }
            }.width(300));

            columns.add(new Column<File>(Date.class, "lastModified")
            {
                @Override
                public Object getValue(File row)
                {
                    return new Date(row.lastModified());
                }
            }.renderer(DateRenderer.instance));

            columns.add(new Column<File>(Integer.class, "size")
            {
                @Override
                public Object getValue(File row)
                {
                    return row.length();
                }
            }.width(100).renderer(SizeRenderer.getInstance()));

        }
        return columns;
    }

    @Override
    public void post()
    {
        super.post();
        getTableModel().update(results);
    }

    @Override
    public ListTableModel<File> getTableModel()
    {
        if ( tableModel == null) {
            tableModel = new ListTableModel<File>( new ArrayList<File>(), getColumns());
        }
        return tableModel;
    }

    @Override
    public File getRow(int row)
    {
        return results.get(row);
    }
    
    @Override
    public JTable createTable()
    {
        JTable result = getColumns().createTable(getTableModel());
        return result;
    }


}
