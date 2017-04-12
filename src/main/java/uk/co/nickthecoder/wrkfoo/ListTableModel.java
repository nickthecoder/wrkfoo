package uk.co.nickthecoder.wrkfoo;

import java.util.List;

public class ListTableModel<R> extends ToolTableModel<R>
{
    private static final long serialVersionUID = 1L;

    private List<R> list;

    public ListTableModel(TableTool<?, ?> tool, List<R> list, Columns<R> columns)
    {
        super(tool, columns);
        this.list = list;
    }

    @Override
    public int getRowCount()
    {
        return list.size();
    }

    @Override
    public R getRow(int row)
    {
        return list.get(row);
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
        fireTableDataChanged();
    }
}
