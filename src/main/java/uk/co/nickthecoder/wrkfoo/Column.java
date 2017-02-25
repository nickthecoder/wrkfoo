package uk.co.nickthecoder.wrkfoo;

import javax.swing.table.TableCellRenderer;

import uk.co.nickthecoder.jguifier.util.Util;

/**
 * 
 * @param <R>
 *            The type of the row, for example, File when using {@link WrkFTask} to list a directory.
 */
public abstract class Column<R>
{
    public Class<?> klass;

    public String key;

    public int width = 150;

    public int minWidth = 100;

    public int maxWidth = 1000;

    public String label;
    
    public boolean editable = false;

    public TableCellRenderer cellRenderer = null;

    public boolean visible = true;
    
    
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

    public Column<R> width(int width)
    {
        this.width = width;
        return this;
    }

    public Column<R> maxWidth(int width)
    {
        this.maxWidth = width;
        return this;
    }

    public Column<R> minWidth(int width)
    {
        this.minWidth = width;
        return this;
    }

    public Column<R> lock()
    {
        this.minWidth = width;
        this.maxWidth = width;
        return this;
    }

    public Column<R> editable()
    {
        this.editable = true;
        return this;
    }
    
    public Column<R> hide()
    {
        this.visible = false;
        return this;
    }
    
    public Column<R> editable(boolean value)
    {
        this.editable = value;
        return this;
    }
    
    public Column<R> renderer(TableCellRenderer tcr)
    {
        this.cellRenderer = tcr;
        return this;
    }
}
