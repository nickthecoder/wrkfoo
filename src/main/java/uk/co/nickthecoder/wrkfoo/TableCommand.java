package uk.co.nickthecoder.wrkfoo;

public interface TableCommand<R> extends Command<R>
{
    public CommandTableModel<R> getTableModel();
}
