package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.Task;

public abstract class SimpleListTool<T extends Task & ListResults<R>, R> extends AbstractListTool<TableResultsPanel<R>, T, R>
{
    public SimpleListTool(T task)
    {
        super(task);
    }
}
