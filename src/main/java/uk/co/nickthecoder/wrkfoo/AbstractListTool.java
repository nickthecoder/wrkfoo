package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;

import uk.co.nickthecoder.jguifier.Task;

public abstract class AbstractListTool<S extends Results, T extends Task & ListResults<R>, R>
    extends AbstractTableTool<S, T, R>
    implements TableTool<S, R>
{
    protected ListTableModel<R> tableModel;

    public AbstractListTool(T task)
    {
        super(task);
    }

    @Override
    public void clearResults()
    {
        if ((getTask() != null) && (getTask().getResults() != null)) {
            getTask().getResults().clear();
        }
    }

    @Override
    public ListTableModel<R> getTableModel()
    {
        if (tableModel == null) {
            tableModel = createTableModel();
        }
        return tableModel;
    }

    protected ListTableModel<R> createTableModel()
    {
        return new ListTableModel<>(this, new ArrayList<R>(), getColumns());
    }

    @Override
    public void updateResults()
    {
        getTableModel().update(getTask().getResults());
        columns.defaultSort(getTable());
    }

}
