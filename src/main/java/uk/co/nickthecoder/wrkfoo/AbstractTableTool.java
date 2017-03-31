package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractTableTool<S extends Results, T extends Task, R>
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

}
