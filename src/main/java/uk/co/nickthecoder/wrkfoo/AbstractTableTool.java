package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractTableTool<T extends Task, R> extends AbstractTool<T> implements TableTool<R>
{
    public AbstractTableTool(T task)
    {
        super(task);
    }

    protected Columns<R> columns;


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

    protected TableToolPanel<R> createToolPanel()
    {
        return new TableToolPanel<R>(this);
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
        return new TableResultsPanel<R>(table);
    }

}
