package uk.co.nickthecoder.wrkfoo.command;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.ListTableModel;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class WrkFBaseCommand extends ListCommand<WrkFTask, File>
{
    public static final Color directoryColor = new Color(255, 255, 230);

    public static Icon directoryIcon = UIManager.getIcon("FileView.directoryIcon");
    public static Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

    {
        try {
            directoryIcon = new ImageIcon( ImageIO.read(MainWindow.class.getResource("folder.png")) );
            fileIcon = new ImageIcon( ImageIO.read(MainWindow.class.getResource("file.png")) );
        } catch (Exception e) {
            // Do nothing - stick with the default icons
        }
    }
    
    /**
     * Amount of characters to chop off of the path column.
     */
    private int chopPath = 0;

    public WrkFBaseCommand(WrkFTask task)
    {
        super(task);
    }

    @Override
    public Icon getIcon()
    {
        return directoryIcon;
    }

    
    @Override
    public String getTitle()
    {
        try {
            return getTask().directory.getValue().getPath();
        } catch (Exception e) {
            return super.getTitle();
        }
    }
    
    @Override
    public String getShortTitle()
    {
        String longTitle = getTitle();

        String shortTitle = longTitle;
        while ( shortTitle.length() > 20 ) {
            int slash = shortTitle.indexOf( File.separator );
            if (slash >= 0) {
                shortTitle = shortTitle.substring(slash + 1);
            } else {
                break;
            }
        }

        return shortTitle;
    }

    protected ListTableModel<File> createTableModel()
    {
        ListTableModel<File> tableModel = new ListTableModel<File>(this, new ArrayList<File>(), getColumns())
        {
            @Override
            public Color getRowBackground(int row)
            {
                File file = list.get(row);
                return file.isFile() ? null : WrkFBaseCommand.directoryColor;
            }
        };

        return tableModel;
    }

    @Override
    public Columns<File> createColumns()
    {
        Columns<File> columns = new Columns<File>();

        columns = new Columns<File>();

        columns.add(new Column<File>(Icon.class, "")
        {
            @Override
            public Icon getValue(File row)
            {
                return row.isDirectory() ? WrkF.directoryIcon : WrkF.fileIcon;
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

        return columns;
    }

    public void updateResults()
    {
        chopPath = getTask().directory.getValue().getPath().length() + 1;
        super.updateResults();
    }

    protected String[] additionalOptionsNames()
    {
        return new String[] { "wrkf" };
    }
}
