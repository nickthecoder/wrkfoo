package uk.co.nickthecoder.wrkfoo;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;


public class SimpleTable<R> extends JTable
{

    private Color oddRowColor = new Color(247, 247, 247);

    @SuppressWarnings("unchecked")
    public SimpleTableModel<R> getModel()
    {
        return (SimpleTableModel<R>) super.getModel();
    }
    
    public SimpleTable(SimpleTableModel<R> model)
    {
        super(model);
        
        InputMap im = this.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = this.getActionMap();

        im.put(KeyStroke.getKeyStroke("TAB"), "Action.tab");
        am.put("Action.tab", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                tab();
            }
        });
        im.put(KeyStroke.getKeyStroke("shift TAB"), "Action.untab");
        am.put("Action.untab", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                untab();
            }
        });
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column)
    {
        int modelColumn = convertColumnIndexToModel(column);

        TableCellRenderer result = getModel().columns.getColumn(modelColumn).cellRenderer;
        return result == null ? super.getCellRenderer(row, column) : result;
    }
    
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
    {
        Component comp = super.prepareRenderer(renderer, row, column);

        if (!isRowSelected(row)) {
            
            Color bg = getModel().getRowBackground( row );
            if ( bg == null) {
                
                if (row % 2 == 0) {
                    comp.setBackground(this.getBackground());
                } else {
                    comp.setBackground(oddRowColor);
                }
            } else {
                comp.setBackground(bg);
            }
        }
        return comp;

    }

    public void tab()
    {
        int row = getSelectedRow();
        int col = getSelectedColumn();

        // Make sure we start with legal values.
        if (col < 0) {
            col = 0;
        }
        if (row < 0) {
            row = 0;
        }

        int startRow = row;

        // Find the next editable cell.
        do {
            col++;
            if (col >= getColumnCount()) {
                col = 0;
                row++;
                if (row >= getRowCount()) {
                    row = 0;
                }
                // Prevent an endless loop if no cells are editable
                if (row == startRow) {
                    return;
                }
            }

        } while (!isCellEditable(row, col));

        // Select the cell in the table.
        tabToCell(row, col);
    }

    public void untab()
    {
        int row = getSelectedRow();
        int col = getSelectedColumn();

        // Make sure we start with legal values.
        if (col < 0) {
            col = 0;
        }
        if (row < 0) {
            row = 0;
        }

        int startRow = row;

        // Find the previous editable cell.
        do {
            col--;
            if (col < 0) {
                col = getColumnCount() - 1;
                row--;
                if (row < 0) {
                    row = getRowCount() - 1;
                }
                // Prevent an endless loop if no cells are editable
                if (row == startRow) {
                    return;
                }
            }

        } while (!isCellEditable(row, col));

        // Select the cell in the table.
        tabToCell(row, col);
    }

    private void tabToCell(final int row, final int col)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                changeSelection(row, col, false, false);
            }
        });
        stopEditing();
    }

    public void stopEditing()
    {
        if (isEditing()) {
            if (isEditing() && !getCellEditor().stopCellEditing()) {
                getCellEditor().cancelCellEditing();
            }
        }
    }
}