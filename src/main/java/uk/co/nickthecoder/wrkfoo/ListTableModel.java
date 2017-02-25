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
    public boolean isCellEditable(int row, int col)
    {
        return columns.getColumn(col).editable;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (columnIndex == 0) {
            return options[rowIndex];
        } else {
            R row = list.get(rowIndex);
            Column<R> column = columns.getColumn(columnIndex);  
            return column.getValue(row);
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex)
    {
        if (columnIndex == 0) {
            options[rowIndex] = (String) value;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
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

    private String[] options = new String[] {};

    public void update(List<R> results)
    {
        this.list = results;
        this.options = new String[results.size()];
        this.fireTableDataChanged();
    }
}
