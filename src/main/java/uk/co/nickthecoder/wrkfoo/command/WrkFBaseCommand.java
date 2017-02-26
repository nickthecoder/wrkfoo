package uk.co.nickthecoder.wrkfoo.command;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.UIManager;

import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.ListTableModel;
import uk.co.nickthecoder.wrkfoo.Option;
import uk.co.nickthecoder.wrkfoo.Options;
import uk.co.nickthecoder.wrkfoo.SimpleTable;
import uk.co.nickthecoder.wrkfoo.TaskCommand;

public class WrkFBaseCommand extends TaskCommand<WrkFTask, File>
{
    public static final Color directoryColor = new Color(255, 255, 230);

    public static final Icon directoryIcon = UIManager.getIcon("FileView.directoryIcon");
    public static final Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

    private ListTableModel<File> tableModel;

    public WrkFBaseCommand(WrkFTask task)
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
            tableModel = new ListTableModel<File>(this, new ArrayList<File>(), getTask().getColumns()) {
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
        SimpleTable<File> result = getTask().getColumns().createTable(getTableModel());
        return result;
    }

    public void updateResults()
    {
        getTableModel().update(getTask().results);
    }


}
