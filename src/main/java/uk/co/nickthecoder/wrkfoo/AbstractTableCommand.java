package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractTableCommand<T extends Task, R> extends AbstractCommand<T> implements TableCommand<R>
{
    public AbstractTableCommand(T task)
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

    protected TableCommandPanel<R> createCommandPanel()
    {
        return new TableCommandPanel<R>(this);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public TableCommandPanel<R> getCommandPanel()
    {
        return (TableCommandPanel<R>) super.getCommandPanel();
    }


    @Override
    public TableResultsPanel<R> createResultsComponent()
    {
        SimpleTable<R> table = getColumns().createTable(getTableModel());
        return new TableResultsPanel<R>(table);
    }

}
