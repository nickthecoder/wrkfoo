package uk.co.nickthecoder.wrkfoo;

public interface TableTool<S extends Results,R> extends Tool<S>
{
    public ToolTableModel<R> getTableModel();

    public Columns<R> getColumns();

    public void clearResults();
    
    public SimpleTable<R> getTable();
}
