package uk.co.nickthecoder.wrkfoo;

public interface TableTool<S extends TableResultsPanel<R>,R> extends Tool<S>
{
    public ToolTableModel<R> getTableModel();

    public Columns<R> getColumns();

    public void clearResults();
}
