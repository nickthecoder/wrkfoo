package uk.co.nickthecoder.wrkfoo;

import javax.swing.table.AbstractTableModel;

public abstract class SimpleTableModel<R> extends AbstractTableModel
{
    private String[] options = new String[] {};

    public Columns<R> columns;

    public SimpleTableModel(Columns<R> columns)
    {
        this.columns = columns;
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
    public void setValueAt(Object value, int rowIndex, int columnIndex)
    {
        if (columnIndex == 0) {
            options[rowIndex] = (String) value;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (columnIndex == 0) {
            return options[rowIndex];
        } else {
            return null;
        }
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

    public void update( int rowCount )
    {
        this.options = new String[rowCount];
        this.fireTableDataChanged();
    }
}
