package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.guiutil.Places.Place;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class PlacesTool extends AbstractListTool<PlacesTask, Place>
{
    public static final Icon icon = Resources.icon("places.png");

    public PlacesTool()
    {
        super(new PlacesTask());
    }

    @Override
    public String getOptionsName()
    {
        return "places";
    }

    @Override
    public String getTitle()
    {
        return "Places";
    }

    @Override
    protected Columns<Place> createColumns()
    {
        Columns<Place> columns = new Columns<>();

        columns = new Columns<>();

        columns.add(new Column<Place>(Icon.class, "")
        {
            @Override
            public Icon getValue(Place row)
            {
                return WrkFBase.getIconForFile(row.file);
            }
        }.width(25).lock());

        columns.add(new Column<Place>(File.class, "file")
        {
            @Override
            public File getValue(Place row)
            {
                return row.file;
            }
        }.hide());

        columns.add(new Column<Place>(String.class, "label")
        {
            @Override
            public String getValue(Place row)
            {
                return row.label;
            }
        }.width(150));

        columns.add(new Column<Place>(String.class, "path")
        {
            @Override
            public String getValue(Place row)
            {
                return row.file.getPath();
            }
        }.tooltip(4).width(500));

        columns.add(new Column<Place>(Date.class, "lastModified")
        {
            @Override
            public Date getValue(Place row)
            {
                return new Date(row.file.lastModified());
            }
        }.width(120).lock().renderer(DateRenderer.instance));

        columns.add(new Column<Place>(Long.class, "size")
        {
            @Override
            public Long getValue(Place row)
            {
                return row.file.length();
            }
        }.width(120).minWidth(80).renderer(SizeRenderer.getInstance()));

        return columns;
    }

    @Override
    public Icon getIcon()
    {
        return icon;
    }

    public Task add()
    {
        Task addTask = new AddTask();

        return addTask;
    }

    public class AddTask extends Task
    {
        public final FileParameter file = new FileParameter.Builder("file").fileOrDirectory()
            .value(Resources.getInstance().getHomeDirectory())
            .parameter();

        public final StringParameter placeName = new StringParameter.Builder("placeName").optional().parameter();

        public AddTask()
        {
            super();
            addParameters(file, placeName);
        }

        @Override
        public void body()
        {
            try {
                new Exec("echo", "file://" + file.getValue().getPath(), placeName.getValue())
                    .stdout(task.store.getValue(), true) // append to the Places file
                    .run();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PlacesTool.this.go();
        }
    };
}
