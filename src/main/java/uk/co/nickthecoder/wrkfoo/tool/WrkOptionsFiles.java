package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.Date;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.DirectoryTool;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptionsFilesTask.WrkOptionsFile;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;


public class WrkOptionsFiles extends AbstractListTool<WrkOptionsFilesTask, WrkOptionsFile> implements DirectoryTool
{

    public WrkOptionsFiles()
    {
        super(new WrkOptionsFilesTask());
    }

    @Override
    public Icon getIcon()
    {
        return Resources.icon("options.png");
    }
    
    @Override
    public String getLongTitle()
    {
        return "WrkOptionsFiles " + getTitle();
    }

    @Override
    protected Columns<WrkOptionsFile> createColumns()
    {
        Columns<WrkOptionsFile> columns = new Columns<WrkOptionsFile>();
        columns.add(new Column<WrkOptionsFile>(File.class, "file")
        {
            @Override
            public File getValue(WrkOptionsFile row)
            {
                return row.file;
            }
        }.tooltip(1).hide().width(300));

        columns.add(new Column<WrkOptionsFile>(File.class, "name")
        {
            @Override
            public String getValue(WrkOptionsFile row)
            {
                return row.file.getName();
            }
        }.tooltip(1).sort().width(300));

        columns.add(new Column<WrkOptionsFile>(Date.class, "lastModified")
        {
            @Override
            public Date getValue(WrkOptionsFile row)
            {
                return new Date(row.file.lastModified());
            }
        }.width(120).lock().renderer(DateRenderer.instance));

        return columns;
    }

    @Override
    public File getDirectory()
    {
        return task.directory.getValue();
    }
}
