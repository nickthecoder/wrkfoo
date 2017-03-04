package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.Parameter;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.TabSetData;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class WrkTabSets extends WrkFBase
{

    public static WrkFTask createTask()
    {
        WrkFTask task = new WrkFTask();

        for (Parameter parameter : task.getParameters().getChildren()) {
            parameter.visible = false;
        }

        task.directory.setValue(Resources.instance.getTabsDirectory());
        task.directory.visible = true;

        task.fileExtensions.setValue("json");
        task.fileExtensions.visible = true;

        return task;
    }

    public WrkTabSets()
    {
        super(createTask());
    }

    @Override
    public Icon getIcon()
    {
        return Resources.icon("workTabSets.png");
    }

    @Override
    public String getTitle()
    {
        return "Tab Sets";
    }

    @Override
    public Columns<File> createColumns()
    {
        Columns<File> columns = new Columns<File>();

        columns = new Columns<File>();

        columns.add(new Column<File>(String.class, "name")
        {
            @Override
            public String getValue(File row)
            {
                return row.getName();
            }
        }.tooltip(2).width(300));

        return columns;
    }

    @Override
    protected String optionsName()
    {
        return "wrktabsets";
    }

    public void load(File file)
    {
        Gson gson = new Gson();

        JsonReader reader;
        try {
            reader = new JsonReader(new FileReader(file));
            TabSetData tsd = gson.fromJson(reader, TabSetData.class);
            MainWindow mainWindow = tsd.createMainWindow();
            mainWindow.pack();
            mainWindow.setVisible(true);

        } catch (FileNotFoundException e) {
            // TODO Report exception
            e.printStackTrace();
        }

    }
}
