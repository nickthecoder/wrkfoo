package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.Task;

public abstract class SimpleWrkFoo<T extends Task & Results<R>, R> implements WrkFoo<R>
{
    public T task;

    protected List<Column<R>> columns;

    public SimpleWrkFoo(T task, String... columnNames)
    {
        this.task = task;

        this.columns = new ArrayList<Column<R>>();
        Map<String, Column<R>> columnMap = task.columnMap();
        for (String columnName : columnNames) {
            this.columns.add(columnMap.get(columnName));
        }
    }

    public Task getTask()
    {
        return task;
    }

    @Override
    public String getTitle()
    {
        return task.getName();
    }

    @Override
    public GroupParameter getParameters()
    {
        return task.getParameters();
    }
    
    public List<Column<R>> getColumns()
    {
        return columns;
    }

    @Override
    public void run()
    {
        task.run();
    }

    public Results<R> getResults()
    {
        return task;
    }

}
