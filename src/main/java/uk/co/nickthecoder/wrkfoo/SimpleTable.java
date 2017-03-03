package uk.co.nickthecoder.wrkfoo;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;

public class SimpleTable<R> extends JTable
{

    private Color oddRowColor = new Color(247, 247, 247);

    public SimpleTable(CommandTableModel<R> model)
    {
        super(model);

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

    @SuppressWarnings("unchecked")
    public CommandTableModel<R> getModel()
    {
        return (CommandTableModel<R>) super.getModel();
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

            Color bg = getModel().getRowBackground(convertRowIndexToModel(row));
            if (bg == null) {

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

    /**
     * When the tab key is pressed, advance to the next editable cell.
     */
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

    /**
     * When the shift-tab key is pressed, advance to the previous editable cell.
     */
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

    @Override
    public String getToolTipText(MouseEvent e)
    {
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int columnIndex = columnAtPoint(p);

        if ((rowIndex >= 0) && (columnIndex >= 0)) {

            columnIndex = convertColumnIndexToModel(columnIndex);
            rowIndex = convertRowIndexToModel(rowIndex);
            
            Column<?> column = getModel().columns.getColumn(columnIndex);
            int tooltipColumn = column.tooltipColumn;
            if (tooltipColumn >= 0) {
                Object val = getModel().getValueAt(rowIndex, tooltipColumn); 
                return val == null ? null : val.toString();
            }
        }

        return super.getToolTipText(e);
    }
}