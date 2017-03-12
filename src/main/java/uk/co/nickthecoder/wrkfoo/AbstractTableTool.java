package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractTableTool<T extends Task, R> extends AbstractTool<T> implements TableTool<R>
{
    public AbstractTableTool(T task)
    {
        super(task);
    }

    protected Columns<R> columns;

    @Override
    public Columns<R> getColumns()
    {
        if (columns == null) {
            columns = createColumns();
        }
        return columns;
    }

    protected abstract Columns<R> createColumns();

    @Override
    public void detach()
    {
        super.detach();
        this.columns = null;
        this.clearResults();
    }

    @Override
    protected TableToolPanel<R> createToolPanel()
    {
        return new TableToolPanel<>(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TableToolPanel<R> getToolPanel()
    {
        return (TableToolPanel<R>) super.getToolPanel();
    }

    @Override
    public TableResultsPanel<R> createResultsComponent()
    {
        SimpleTable<R> table = getColumns().createTable(getTableModel());
        return new TableResultsPanel<>(table);
    }

}
