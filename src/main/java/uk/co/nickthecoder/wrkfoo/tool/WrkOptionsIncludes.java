package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.StringParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.AbstractListTool;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Resources;

public class WrkOptionsIncludes extends AbstractListTool<WrkOptionsIncludesTask, String>
{
    public static WrkOptionsIncludesTask createTask(File file)
    {
        WrkOptionsIncludesTask task = new WrkOptionsIncludesTask();
        task.optionsFile.setDefaultValue(file);
        return task;
    }

    public WrkOptionsIncludes()
    {
        super(new WrkOptionsIncludesTask());
    }

    public WrkOptionsIncludes(File optionsFile)
    {
        super(createTask(optionsFile));
    }

    @Override
    public String getLongTitle()
    {
        try {
            return "Includes : " + task.optionsFile.getValue().getName();
        } catch (Exception e) {
            return super.getLongTitle();
        }
    }

    @Override
    public String getShortTitle()
    {
        try {
            return task.optionsFile.getValue().getName();
        } catch (Exception e) {
            return super.getShortTitle();
        }
    }

    @Override
    protected Columns<String> createColumns()
    {
        Columns<String> columns = new Columns<>();

        columns.add(new Column<String>(String.class, "include")
        {
            @Override
            public String getValue(String row)
            {
                return row;
            }
        }.width(300));

        return columns;
    }

    public void save() throws FileNotFoundException
    {
        task.optionsData.save();
    }

    @Override
    public Icon getIcon()
    {
        return Resources.icon("options.png");
    }

    public Task addInclude()
    {
        return new AddInclude();
    }

    public Task removeInclude(String name)
    {
        return new RemoveInclude(name);
    }

    class AddInclude extends Task
    {
        StringParameter include = new StringParameter.Builder("include").parameter();

        public AddInclude()
        {
            super();
            addParameters(include);
        }

        @Override
        public void body()
        {
            WrkOptionsIncludes.this.task.optionsData.include.add(include.getValue());
            try {
                save();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class RemoveInclude extends Task
    {
        StringParameter remove = new StringParameter.Builder("remove").parameter();

        public RemoveInclude(String name)
        {
            super();
            addParameters(remove);
            remove.setDefaultValue(name);
        }

        @Override
        public void body()
        {
            WrkOptionsIncludes.this.task.optionsData.include.remove(remove.getValue());
            try {
                save();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
