package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class SimpleListTool<T extends Task & ListResults<R>, R>
    extends AbstractListTool<TableResults<R>, T, R>
{
    public SimpleListTool(T task)
    {
        super(task);
    }
    
    @Override
    public TableResults<R> createResultsPanel()
    {
        SimpleTable<R> table = getColumns().createTable(getTableModel());

        if (dragListConverter != null) {
            dragListConverter.createDragListHandler(table);
        }

        return new TableResults<>(this, table);
    }

    @Override
    public SimpleTable<R> getTable()
    {
        return getResultsPanel().getTable();
    }
}
