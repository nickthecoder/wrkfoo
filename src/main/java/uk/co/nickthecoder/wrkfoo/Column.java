package uk.co.nickthecoder.wrkfoo;

import javax.swing.table.TableCellRenderer;

import uk.co.nickthecoder.jguifier.util.Util;

public abstract class Column<R>
{
    public Class<?> klass;
    
    public String key;

    public String label;
    
    public TableCellRenderer cellRenderer = null;

    public abstract Object getValue(R row);

    public Column(Class<?> klass, String key)
    {
        this(klass, key, Util.uncamel(key));
    }

    public Column(Class<?> klass, String key, String label)
    {
        this.klass = klass;
        this.key = key;
        this.label = label;
    }
    
    public Column<R> renderer( TableCellRenderer tcr )
    {
        this.cellRenderer = tcr;
        return this;
    }
}
