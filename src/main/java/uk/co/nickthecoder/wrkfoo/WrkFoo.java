package uk.co.nickthecoder.wrkfoo;

import java.util.List;

import uk.co.nickthecoder.jguifier.GroupParameter;

public interface WrkFoo<R> extends Runnable
{
    public String getTitle();

    public List<Column<R>> getColumns();

    public GroupParameter getParameters();

    public void run();
    
    public Results<R> getResults();
}
