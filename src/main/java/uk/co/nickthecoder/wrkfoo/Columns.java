package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

public class Columns<R>
{
    private List<Column<R>> columns;

    public Column<R> optionColunm;

    public int defaultSortColumnIndex = -1;

    public Columns()
    {
        columns = new ArrayList<Column<R>>();

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

    public void add(Column<R> column)
    {
        if (column.defaultSort) {
            defaultSortColumnIndex = columns.size();
        }
        columns.add(column);
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
        SimpleTable<R> table = new SimpleTable<R>(tableModel);
        TableColumnModel tcm = table.getColumnModel();

        for (int i = 0; i < getColumnCount(); i++) {
            TableColumn tableColumn = tcm.getColumn(i);
            Column<?> column = getColumn(i);

            tableColumn.setPreferredWidth(column.width);
            tableColumn.setMinWidth(column.minWidth);
            tableColumn.setMaxWidth(column.maxWidth);
        }

        for (int i = getColumnCount() - 1; i >= 0; i--) {
            TableColumn tableColumn = tcm.getColumn(i);
            Column<?> column = getColumn(i);
            if (!column.visible) {
                tcm.removeColumn(tableColumn);
            }
        }

        // table.setAutoCreateRowSorter(true);
        return table;
    }

    public void defaultSort(JTable table)
    {
        TableRowSorter<?> rowSorter = (TableRowSorter<?>) table.getRowSorter();

        for (int i = getColumnCount() - 1; i >= 0; i--) {
            Column<?> column = columns.get(i);
            if ( column.comparator != null ) {
                rowSorter.setComparator(i, column.comparator);
            }
        }
        
        if (defaultSortColumnIndex > 0) {
            
            List<SortKey> keys = new ArrayList<SortKey>();
            SortKey sortKey = new SortKey(defaultSortColumnIndex,
                columns.get(defaultSortColumnIndex).reverse ?
                    SortOrder.DESCENDING: SortOrder.ASCENDING  );
            keys.add(sortKey);
            rowSorter.setSortKeys(keys);
            
        }
    }
}
