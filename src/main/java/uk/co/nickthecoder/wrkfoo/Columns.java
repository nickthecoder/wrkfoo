package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class Columns<R>
{
    private List<Column<R>> columns;

    public Column<R> optionColunm;

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

    public SimpleTable<R> createTable(CommandTableModel<R> tableModel)
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

        return table;
    }

}
