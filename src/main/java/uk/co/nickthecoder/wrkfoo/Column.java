package uk.co.nickthecoder.wrkfoo;

import java.util.Comparator;

import javax.swing.table.TableCellRenderer;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.tool.ExportTableData;
import uk.co.nickthecoder.wrkfoo.tool.WrkFTask;

/**
 * 
 * @param <R>
 *            The type of the row, for example, File when using {@link WrkFTask} to list a directory.
 */
public abstract class Column<R>
{
    private Columns<R> columns;

    public Class<?> klass;

    public final String key;

    public int width = 150;

    public int minWidth = 10;

    public int maxWidth = 1000;

    public String label;

    public boolean defaultSort = false;

    public boolean reverse = false;

    public boolean editable = false;

    public TableCellRenderer cellRenderer = null;

    public boolean visible = true;

    public Comparator<?> comparator;

    public int tooltipColumn = -1;

    public boolean save = true;

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

    public String getKey()
    {
        return key;
    }

    void setColumns(Columns<R> columns)
    {
        this.columns = columns;
    }

    public Columns<R> getColumns()
    {
        return columns;
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

    public Column<R> sort()
    {
        this.reverse = false;
        this.defaultSort = true;
        if (getColumns() != null) {
            getColumns().defaultSortColumnIndex = getColumns().indexOf(key);
        }
        return this;
    }

    public Column<R> reverseSort()
    {
        this.reverse = true;
        this.defaultSort = true;
        return this;
    }

    public Column<R> comparator(Comparator<?> value)
    {
        comparator = value;
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

    public Column<R> tooltip()
    {
        return tooltip(key);
    }

    public Column<R> tooltip(String columnKey)
    {
        tooltipColumn = getColumns().indexOf(columnKey);
        return this;
    }

    public Column<R> tooltipFor(String columnKey)
    {
        Column<R> other = getColumns().findColumn(columnKey);
        other.tooltipColumn = getColumns().indexOf(key);

        return this;
    }

    /**
     * Column is not saved by {@link ExportTableData}. Use this for icons and other non-data columns.
     * 
     * @return this
     */
    public Column<R> trivial()
    {
        this.save = false;
        return this;
    }
}
