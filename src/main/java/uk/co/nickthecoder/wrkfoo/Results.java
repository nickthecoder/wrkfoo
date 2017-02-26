package uk.co.nickthecoder.wrkfoo;

public interface Results<R>
{    
    public Columns<R> getColumns();
    
    public R getRow(int row);
    
    public void clearResults();
}
