package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.util.Date;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.ListCommand;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.command.PlacesTask.PlacesWrappedFile;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class Places extends ListCommand<PlacesTask, PlacesWrappedFile>
{
    public static final Icon icon = Resources.icon("places.png");

    public Places()
    {
        super(new PlacesTask());
    }

    @Override
    protected Columns<PlacesWrappedFile> createColumns()
    {
        Columns<PlacesWrappedFile> columns = new Columns<PlacesWrappedFile>();

        columns = new Columns<PlacesWrappedFile>();

        columns.add(new Column<PlacesWrappedFile>(Icon.class, "")
        {
            @Override
            public Icon getValue(PlacesWrappedFile row)
            {
                return row.file.isDirectory() ? WrkF.directoryIcon : WrkF.fileIcon;
            }
        }.width(25).lock());

        columns.add(new Column<PlacesWrappedFile>(File.class, "file")
        {
            @Override
            public File getValue(PlacesWrappedFile row)
            {
                return row.file;
            }
        }.hide());

        columns.add(new Column<PlacesWrappedFile>(String.class, "name")
        {
            @Override
            public String getValue(PlacesWrappedFile row)
            {
                return row.name;
            }
        }.width(150));

        columns.add(new Column<PlacesWrappedFile>(String.class, "path")
        {
            @Override
            public String getValue(PlacesWrappedFile row)
            {
                return row.getChoppedPath();
            }
        }.tooltip(4).width(500));

        columns.add(new Column<PlacesWrappedFile>(Date.class, "lastModified")
        {
            @Override
            public Date getValue(PlacesWrappedFile row)
            {
                return new Date(row.file.lastModified());
            }
        }.width(120).lock().renderer(DateRenderer.instance));

        columns.add(new Column<PlacesWrappedFile>(Long.class, "size")
        {
            @Override
            public Long getValue(PlacesWrappedFile row)
            {
                return row.file.length();
            }
        }.width(120).minWidth(80).renderer(SizeRenderer.getInstance()));

        return columns;
    }

    public void updateResults()
    {
        super.updateResults();
    }

    @Override
    protected String optionsName()
    {
        return "places";
    }

    @Override
    public Icon getIcon()
    {
        return icon;
    }
}
