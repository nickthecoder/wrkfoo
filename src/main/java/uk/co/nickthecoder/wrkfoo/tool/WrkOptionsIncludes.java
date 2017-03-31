package uk.co.nickthecoder.wrkfoo.tool;

import java.net.URL;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.Column;
import uk.co.nickthecoder.wrkfoo.Columns;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.SimpleListTool;

public class WrkOptionsIncludes extends SimpleListTool<WrkOptionsIncludesTask, String>
{
    public WrkOptionsIncludes(WrkOptionsIncludesTask task)
    {
        super(task);
    }

    public WrkOptionsIncludes()
    {
        super(new WrkOptionsIncludesTask());
    }

    public WrkOptionsIncludes(URL path, String name)
    {
        super(new WrkOptionsIncludesTask(path, name));
    }

    @Override
    public String getLongTitle()
    {
        return task.path.getValue() + " : " + task.optionsName.getValue();
    }

    @Override
    public String getShortTitle()
    {
        return task.optionsName.getValue();
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

    public void save()
    {
        task.optionsData.save();
        task.optionsData.reload();
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
            save();
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
            save();
        }
    }
}
