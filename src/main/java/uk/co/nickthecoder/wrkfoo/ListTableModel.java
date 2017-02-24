package uk.co.nickthecoder.wrkfoo;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ListTableModel<R> extends AbstractTableModel
{
    public List<R> list;

    public Columns<R> columns;

    public ListTableModel(List<R> list, Columns<R> columns)
    {
        this.list = list;
        this.columns = columns;
    }

    @Override
    public int getRowCount()
    {
        return list.size();
    }

    @Override
    public int getColumnCount()
    {
        return columns.getColumnCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return columns.getColumn(columnIndex).getValue(list.get(rowIndex));
    }

    @Override
    public String getColumnName(int col)
    {
        return columns.getColumn(col).label;
    }

    @Override
    public Class<?> getColumnClass(int col)
    {
        return columns.getColumn(col).klass;
    }

    public void update(List<R> results)
    {
        this.list = results;
        this.fireTableDataChanged();        
    }
}
