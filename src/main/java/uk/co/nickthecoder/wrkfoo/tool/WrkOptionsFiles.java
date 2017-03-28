package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.DirectoryTool;
import uk.co.nickthecoder.wrkfoo.DragFileConverter;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.tool.WrkOptionsFilesTask.WrkOptionsFile;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;

public class WrkOptionsFiles extends AbstractListTool<WrkOptionsFilesTask, WrkOptionsFile> implements DirectoryTool
{

    public WrkOptionsFiles()
    {
        super(new WrkOptionsFilesTask());
        dragListConverter = new DragFileConverter<WrkOptionsFile>();
    }

    @Override
    public Icon getIcon()
    {
        return Resources.icon("options.png");
    }

    @Override
    public String getLongTitle()
    {
        return super.getLongTitle() + " " + task.directory.getValue();
    }

    @Override
    protected Columns<WrkOptionsFile> createColumns()
    {
        Columns<WrkOptionsFile> columns = new Columns<>();
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

    public WrkOptionsIncludes wrkOptionsIncludes(File file)
    {
        URL path;
        try {
            path = file.getParentFile().toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String name = Util.removeExtension(file);
        return new WrkOptionsIncludes(path, name);
    }

    public WrkOptions wrkOptions(File file)
    {
        URL path;
        try {
            path = file.getParentFile().toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String name = Util.removeExtension(file);

        return new WrkOptions(path, name);
    }

    @Override
    public File getDirectory()
    {
        return task.directory.getValue();
    }
}
