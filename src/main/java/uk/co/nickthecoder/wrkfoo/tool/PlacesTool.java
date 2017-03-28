package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.guiutil.Places.Place;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.DragFileConverter;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.util.DateRenderer;
import uk.co.nickthecoder.wrkfoo.util.SizeRenderer;

public class PlacesTool extends AbstractListTool<PlacesTask, Place>
{
    public PlacesTool()
    {
        super(new PlacesTask());
        this.dragListConverter = new DragFileConverter<Place>();
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

    public Task add()
    {
        Task addTask = new AddTask();

        return addTask;
    }

    public Task remove(Place place)
    {
        RemoveTask task = new RemoveTask();
        task.file.setValue(place.file);
        task.label.setValue(place.label);
        return task;
    }

    public Task edit(Place place)
    {
        EditTask task = new EditTask();
        task.oldFile.setDefaultValue(place.file);
        task.file.setDefaultValue(place.file);
        task.label.setDefaultValue(place.label);
        return task;
    }

    public class AddTask extends Task
    {
        public final FileParameter file = new FileParameter.Builder("file").fileOrDirectory()
            .value(Resources.getInstance().getHomeDirectory())
            .parameter();

        public final StringParameter label = new StringParameter.Builder("label").optional().parameter();

        public AddTask()
        {
            super();
            addParameters(file, label);
        }

        @Override
        public void body()
        {
            try {
                new Exec("echo", "file://" + file.getValue().getPath(), label.getValue())
                    .stdout(task.store.getValue(), true) // append to the Places file
                    .run();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PlacesTool.this.go();
        }
    };

    public class RemoveTask extends Task
    {
        public final FileParameter file = new FileParameter.Builder("file").fileOrDirectory()
            .value(Resources.getInstance().getHomeDirectory())
            .parameter();

        public final StringParameter label = new StringParameter.Builder("label").optional().parameter();

        public RemoveTask()
        {
            super();
            addParameters(file, label);
        }

        @Override
        public void body()
        {
            List<Place> places = getTask().getResults();
            try {
                PrintWriter out = new PrintWriter(getTask().store.getValue());

                for (Place place : places) {
                    if (Util.equals(place.file, file.getValue())) {
                        continue;
                    }
                    out.print("file://");
                    out.print(place.file.getPath());
                    if (!Util.empty(place.label)) {
                        out.print(" ");
                        out.println(place.label);
                    } else {
                        out.println();
                    }
                }

                out.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    };

    public class EditTask extends Task
    {
        public final FileParameter oldFile = new FileParameter.Builder("oldFile").fileOrDirectory()
            .hide().parameter();

        public final FileParameter file = new FileParameter.Builder("file").fileOrDirectory()
            .parameter();

        public final StringParameter label = new StringParameter.Builder("label").optional().parameter();

        public EditTask()
        {
            super();
            addParameters(oldFile, file, label);
        }

        @Override
        public void body()
        {
            List<Place> places = getTask().getResults();
            try {
                PrintWriter out = new PrintWriter(getTask().store.getValue());

                for (Place place : places) {

                    if (Util.equals(place.file, oldFile.getValue())) {

                        out.print("file://");
                        out.print(file.getValue().getPath());
                        if (!Util.empty(label.getValue())) {
                            out.print(" ");
                            out.println(label.getValue());
                        } else {
                            out.println();
                        }

                    } else {
                        out.print("file://");
                        out.print(place.file.getPath());
                        if (!Util.empty(place.label)) {
                            out.print(" ");
                            out.println(place.label);
                        } else {
                            out.println();
                        }
                    }
                }

                out.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    };
}
