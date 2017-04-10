package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import uk.co.nickthecoder.jguifier.util.Util;

public class Columns<R> implements Iterable<Column<R>>
{
    private List<Column<R>> columns;

    public Column<R> optionColunm;

    public int defaultSortColumnIndex = -1;

    public Columns()
    {
        columns = new ArrayList<>();

        optionColunm = new Column<R>(String.class, "")
        {
            @Override
            public String getValue(R row)
            {
                return "";
            }
        }.width(50).lock().editable();
        columns.add(optionColunm);
    }

    public int indexOf(Column<?> column)
    {
        return columns.indexOf(column);
    }

    public int indexOf(String columnKey)
    {
        int index = 0;
        for (Column<R> c : columns) {
            if (Util.equals(c.getKey(), columnKey)) {
                return index;
            }
            index++;
        }
        throw new IllegalArgumentException("Column '" + columnKey + "' not found.");
    }

    public Column<R> findColumn(String columnKey)
    {
        for (Column<R> c : columns) {
            if (Util.equals(c.getKey(), columnKey)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Column '" + columnKey + "' not found.");
    }

    public Column<R> add(Column<R> column)
    {
        if (column.defaultSort) {
            defaultSortColumnIndex = columns.size();
        }
        columns.add(column);
        column.setColumns(this);

        return column;
    }

    public int getColumnCount()
    {
        return columns.size();
    }

    public Column<R> getColumn(int i)
    {
        return columns.get(i);
    }

    public Column<R> find(String name)
    {
        for (Column<R> column : columns) {
            if (column.key == name) {
                return column;
            }
        }
        return null;
    }

    public SimpleTable<R> createTable(ToolTableModel<R> tableModel)
    {
        SimpleTable<R> table = new SimpleTable<>(tableModel);

        initialiseColumns(table);
        return table;
    }

    public void initialiseColumns(SimpleTable<R> table)
    {
        TableColumnModel tcm = table.getColumnModel();

        // Remove all the columns
        for (int i = table.getColumnCount() - 1; i >= 0; i--) {
            TableColumn tableColumn = tcm.getColumn(i);
            tcm.removeColumn(tableColumn);
        }

        // Add the visible columns
        for (int i = 0; i < getColumnCount(); i++) {
            Column<?> column = getColumn(i);
            if (column.visible) {
                TableColumn tableColumn = new TableColumn(i);
                tableColumn.setHeaderValue(column.label);
                tableColumn.setPreferredWidth(column.width);
                tableColumn.setMinWidth(column.minWidth);
                tableColumn.setMaxWidth(column.maxWidth);
                tcm.addColumn(tableColumn);
            }
        }
    }

    public void initialiseColumns(SimpleTable<R> table, List<Column<?>> columns)
    {
        TableColumnModel tcm = table.getColumnModel();

        // Remove all the columns
        for (int i = table.getColumnCount() - 1; i >= 0; i--) {
            TableColumn tableColumn = tcm.getColumn(i);
            tcm.removeColumn(tableColumn);
        }

        // Add the visible columns
        for (Column<?> column : columns) {
            int i = indexOf(column);
            TableColumn tableColumn = new TableColumn(i);
            tableColumn.setHeaderValue(column.label);
            tableColumn.setPreferredWidth(column.width);
            tableColumn.setMinWidth(column.minWidth);
            tableColumn.setMaxWidth(column.maxWidth);
            tcm.addColumn(tableColumn);
        }
    }

    public void defaultSort(JTable table)
    {
        TableRowSorter<?> rowSorter = (TableRowSorter<?>) table.getRowSorter();

        for (int i = getColumnCount() - 1; i >= 0; i--) {
            Column<?> column = columns.get(i);
            if (column.comparator != null) {
                rowSorter.setComparator(i, column.comparator);
            }
        }

        if (defaultSortColumnIndex > 0) {

            List<SortKey> keys = new ArrayList<>();
            SortKey sortKey = new SortKey(defaultSortColumnIndex,
                columns.get(defaultSortColumnIndex).reverse ? SortOrder.DESCENDING : SortOrder.ASCENDING);
            keys.add(sortKey);
            rowSorter.setSortKeys(keys);

        }
    }

    @Override
    public Iterator<Column<R>> iterator()
    {
        return columns.iterator();
    }
}
