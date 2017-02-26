package uk.co.nickthecoder.wrkfoo;

import java.util.List;

public class ListTableModel<R> extends CommandTableModel<R>
{
    public List<R> list;


    public ListTableModel(Command<?> command, List<R> list, Columns<R> columns)
    {
        super(command, columns);
        this.list = list;
    }

    @Override
    public int getRowCount()
    {
        return list.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (columnIndex == 0) {
            return super.getValueAt(rowIndex, columnIndex);
        } else {
            R row = list.get(rowIndex);
            Column<R> column = columns.getColumn(columnIndex);  
            return column.getValue(row);
        }
    }

    public void update(List<R> results)
    {
        this.list = results;
        update(results.size());
    }
}
