package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.ListTableModel;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.tool.WrkFTask.WrkFWrappedFile;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;
import uk.co.nickthecoder.wrkfoo.util.FileNameRenderer;
import uk.co.nickthecoder.wrkfoo.util.FoldersFirstComparator;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class WrkFBase extends AbstractListTool<WrkFTask, WrkFWrappedFile>
{
    public static final Color directoryColor = new Color(255, 255, 230);

    public static final Icon fileManagerIcon = Resources.icon("fileManager.png");
    public static final Icon directoryIcon = Resources.icon("folder.png");
    public static final Icon fileIcon = Resources.icon("file.png");

    public WrkFBase(WrkFTask task)
    {
        super(task);
    }

    public static Icon getIconForFile( File file )
    {
        if (file.exists()) {
            return file.isDirectory() ? directoryIcon : fileIcon;
        } else {
            return null;
        }
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
        while (shortTitle.length() > 20) {
            int slash = shortTitle.indexOf(File.separator);
            if (slash >= 0) {
                shortTitle = shortTitle.substring(slash + 1);
            } else {
                break;
            }
        }

        return shortTitle;
    }

    protected ListTableModel<WrkFWrappedFile> createTableModel()
    {
        ListTableModel<WrkFWrappedFile> tableModel = new ListTableModel<WrkFWrappedFile>(this,
            new ArrayList<WrkFWrappedFile>(), getColumns())
        {
            @Override
            public Color getRowBackground(int row)
            {
                File file = list.get(row).file;
                return file.isFile() ? null : WrkFBase.directoryColor;
            }
        };

        return tableModel;
    }

    @Override
    public Columns<WrkFWrappedFile> createColumns()
    {
        Columns<WrkFWrappedFile> columns = new Columns<WrkFWrappedFile>();

        columns = new Columns<WrkFWrappedFile>();

        columns.add(new Column<WrkFWrappedFile>(Icon.class, "")
        {
            @Override
            public Icon getValue(WrkFWrappedFile row)
            {
                return getIconForFile(row.file);
            }
        }.width(25).lock().trivial());

        columns.add(new Column<WrkFWrappedFile>(String.class, "path")
        {
            @Override
            public String getValue(WrkFWrappedFile row)
            {
                return row.getChoppedPath();
            }
        }.tooltip(2).hide().width(300));

        columns.add(new Column<WrkFWrappedFile>(File.class, "name")
        {
            @Override
            public File getValue(WrkFWrappedFile row)
            {
                return row.file;
            }
        }.tooltip(2).comparator(FoldersFirstComparator.instance).sort().width(300).renderer(FileNameRenderer.instance));

        columns.add(new Column<WrkFWrappedFile>(Date.class, "lastModified")
        {
            @Override
            public Date getValue(WrkFWrappedFile row)
            {
                return new Date(row.file.lastModified());
            }
        }.width(120).lock().renderer(DateRenderer.instance));

        columns.add(new Column<WrkFWrappedFile>(Long.class, "size")
        {
            @Override
            public Long getValue(WrkFWrappedFile row)
            {
                return row.file.length();
            }
        }.width(120).minWidth(80).renderer(SizeRenderer.getInstance()));

        return columns;
    }

    @Override
    public void postCreate()
    {
        super.postCreate();

        MainWindow.putAction("alt UP", "upDirectory", getToolPanel(), new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                File parent = getTask().directory.getValue().getParentFile();
                if (parent != null) {
                    getTask().directory.setValue(parent);
                }
                go();
            }
        });
    }

    @Override
    protected String optionsName()
    {
        return "wrkf";
    }
}
