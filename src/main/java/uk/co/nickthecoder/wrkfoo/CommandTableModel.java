package uk.co.nickthecoder.wrkfoo;

import java.awt.Color;

import javax.swing.table.AbstractTableModel;

public abstract class CommandTableModel<R> extends AbstractTableModel
{
    private Command<?> command;
    
    private String[] optionValues = new String[] {};

    public Columns<R> columns;

    public CommandTableModel(Command<?> command, Columns<R> columns)
    {
        this.command = command;
        this.columns = columns;
    }

    public abstract R getRow(int row);
    
    public Command<?> getCommand()
    {
        return command;
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
            optionValues[rowIndex] = (String) value;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (columnIndex == 0) {
            return optionValues[rowIndex];
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

    public Color getRowBackground( int row )
    {
        return null;
    }
    
    public void update( int rowCount )
    {
        this.optionValues = new String[rowCount];
        this.fireTableDataChanged();
    }
}
