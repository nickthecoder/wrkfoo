package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.Icon;
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
        setName( "WrkFCommand" );
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
