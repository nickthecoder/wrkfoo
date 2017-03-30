package uk.co.nickthecoder.wrkfoo;

public interface TableTool<R> extends Tool
{
    public ToolTableModel<R> getTableModel();

    public Columns<R> getColumns();

    public void clearResults();
}
