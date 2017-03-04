package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.util.Date;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.ListCommand;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class Places extends ListCommand<PlacesTask, File>
{
    public static final Icon icon = Resources.icon("places.png");

    public Places()
    {
        super(new PlacesTask());
    }

    @Override
    protected Columns<File> createColumns()
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

        columns.add(new Column<File>(File.class, "file")
        {
            @Override
            public File getValue(File row)
            {
                return row;
            }
        }.hide());

        columns.add(new Column<File>(String.class, "path")
        {
            @Override
            public String getValue(File row)
            {
                String path = row.getPath();
                String prefix = getTask().store.getValue().getParent();
                if ((path.startsWith(prefix)) && (path.length() > prefix.length())) {
                    return path.substring(prefix.length() + 1);
                } else {
                    return path;
                }
            }
        }.tooltip(2).width(500));

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
