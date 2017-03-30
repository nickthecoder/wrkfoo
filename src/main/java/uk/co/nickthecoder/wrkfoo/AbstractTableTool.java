package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractTableTool<S extends TableResultsPanel<R>, T extends Task, R>
    extends AbstractThreadedTool<S, T>
    implements TableTool<S, R>
{
    protected DragListConverter<R, ?> dragListConverter;

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
    public void focusOnResults(int importance)
    {
        if (getResultsPanel().getTable().getModel().getRowCount() == 0) {

            MainWindow.focusLater("Results. No rows", MainWindow.getMainWindow(getToolPanel()).getOptionField(),
                importance);

        } else {
            super.focusOnResults(importance);
        }
    }
}
