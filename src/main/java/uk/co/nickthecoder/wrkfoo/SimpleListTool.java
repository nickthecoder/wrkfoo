package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class SimpleListTool<T extends Task & ListResults<R>, R>
    extends AbstractListTool<TableResultsPanel<R>, T, R>
{
    public SimpleListTool(T task)
    {
        super(task);
    }
    
    @Override
    public TableResultsPanel<R> createResultsPanel()
    {
        SimpleTable<R> table = getColumns().createTable(getTableModel());

        if (dragListConverter != null) {
            dragListConverter.createDragListHandler(table);
        }

        return new TableResultsPanel<>(this, table);
    }

    @Override
    public SimpleTable<R> getTable()
    {
        return getResultsPanel().getTable();
    }
}
