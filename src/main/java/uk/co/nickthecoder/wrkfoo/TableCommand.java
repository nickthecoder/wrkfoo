package uk.co.nickthecoder.wrkfoo;

public interface TableCommand<R> extends Command
{
    public CommandTableModel<R> getTableModel();

    public TableCommandPanel<R> getCommandPanel();
    
    public Columns<R> getColumns();
    
    public void clearResults();
}
