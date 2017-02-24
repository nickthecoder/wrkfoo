package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.TableModel;

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
            });

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
            }.renderer(SizeRenderer.instance));

        }
        return columns;
    }

    @Override
    public void processResults()
    {
        // TODO Split base class, so that this isn't needed. Do nothing
    }

    @Override
    public void post()
    {
        super.post();
        tableModel = new ListTableModel<File>(results, getColumns());
    }

    @Override
    public TableModel getTableModel()
    {
        return tableModel;
    }

    @Override
    public JTable createTable()
    {
        JTable result = getColumns().createTable(getTableModel());
        // result.getColumnModel().getColumn(1).setCellRenderer(cellRenderer);
        return result;
    }

}
