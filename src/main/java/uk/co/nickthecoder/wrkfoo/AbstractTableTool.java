package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractTableTool<T extends Task, R> extends AbstractThreadedTool<T> implements TableTool<R>
{
    protected DragListConverter<R,?> dragListConverter;   

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
        
        if (dragListConverter != null) {
            dragListConverter.createDragListHandler(table);
        }

        return new TableResultsPanel<>(table);
    }

    @Override
    public void focusOnResults(int importance)
    {
        if (getToolPanel().getTable().getModel().getRowCount() == 0) {

            MainWindow.focusLater("Results. No rows", MainWindow.getMainWindow(getToolPanel()).getOptionField(),
                importance);

        } else {
            super.focusOnResults(importance);
        }
    }
}
