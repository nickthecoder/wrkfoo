package uk.co.nickthecoder.wrkfoo;

public interface TableTool<R> extends Tool
{
    public ToolTableModel<R> getTableModel();

    public TableToolPanel<R> getToolPanel();
    
    public Columns<R> getColumns();
    
    public void clearResults();
}
