package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;

import uk.co.nickthecoder.jguifier.Task;

public abstract class ListCommand<T extends Task & ListResults<R>,R> extends AbstractCommand<T,R>
{
    protected ListTableModel<R> tableModel;

    public ListCommand(T task)
    {
        super(task);
    }

    @Override
    public void clearResults()
    {
        if ( ( getTask() != null ) && ( getTask().getResults() != null ) ) {
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
        return new ListTableModel<R>(this, new ArrayList<R>(), getColumns());  
    }
    
    @Override
    public void updateResults()
    {
        getTableModel().update(getTask().getResults());
    }

}
