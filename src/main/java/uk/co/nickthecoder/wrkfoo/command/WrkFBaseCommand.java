package uk.co.nickthecoder.wrkfoo.command;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.UIManager;

import uk.co.nickthecoder.jguifier.util.FileListerTask;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.ListTableModel;
import uk.co.nickthecoder.wrkfoo.Option;
import uk.co.nickthecoder.wrkfoo.Options;
import uk.co.nickthecoder.wrkfoo.SimpleTable;
import uk.co.nickthecoder.wrkfoo.TaskCommand;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class WrkFBaseCommand extends TaskCommand<FileListerTask, File>
{
    public static final Color directoryColor = new Color(255, 255, 230);

    public static final Icon directoryIcon = UIManager.getIcon("FileView.directoryIcon");
    public static final Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

    private Columns<File> columns;

    private ListTableModel<File> tableModel;

    /**
     * Amount of characters to chop off of the path column.
     */
    private int chopPath = 0;

    public WrkFBaseCommand(FileListerTask task)
    {
        super(task);
    }

    @Override
    public Icon getIcon()
    {
        return directoryIcon;
    }

    public String getTitle()
    {
        try {
            return getTask().directory.getValue().getPath();
        } catch (Exception e) {
            return super.getTitle();
        }
    }

    @Override
    public void defaultAction(File file)
    {
        if (file.isDirectory()) {
            getTask().directory.setValue(file);
            go();
        }
    }

    @Override
    public void postCreate()
    {
        Options options = getOptions();

        options.add("", new Option() // The default
            {
                @Override
                public void runOption(Command<?> command, Object row)
                {
                    getTask().directory.setValue(((File) row));
                    go();
                }

            });

        options.add("ls", new Option()
        {
            @Override
            public void runOption(Command<?> command, Object row)
            {
                WrkFCommand wrkF = new WrkFCommand();
                wrkF.getTask().directory.setValue((File) row);
                getCommandTab().update( wrkF );
           }

        });

    }


    @Override
    public ListTableModel<File> getTableModel()
    {
        if (tableModel == null) {
            tableModel = new ListTableModel<File>(this, new ArrayList<File>(), getColumns()) {
                @Override
                public Color getRowBackground( int row )
                {
                    File file = list.get(row);
                    return file.isFile() ? null : WrkFBaseCommand.directoryColor;
                }
            };
        }
        return tableModel;
    }

    @Override
    public SimpleTable<File> createTable()
    {
        SimpleTable<File> result = getColumns().createTable(getTableModel());
        return result;
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

    public void updateResults()
    {
        chopPath = getTask().directory.getValue().getPath().length() + 1;
        getTableModel().update(getTask().results);
    }

    public void clearResults()
    {
        getTask().results.clear();
    }

}
