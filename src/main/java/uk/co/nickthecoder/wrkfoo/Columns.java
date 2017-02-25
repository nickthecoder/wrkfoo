package uk.co.nickthecoder.wrkfoo;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class Columns<R>
{
    private List<Column<R>> columns;

    private Color oddRowColor = new Color(230, 230, 230);

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

    public JTable createTable(TableModel tableModel)
    {
        JTable table = new Table(tableModel);

        for (int i = 0; i < getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(getColumn(i).width);
            column.setMinWidth(getColumn(i).minWidth);
            column.setMaxWidth(getColumn(i).maxWidth);
        }

        return table;
    }

    public class Table extends JTable
    {
        public Table(TableModel model)
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

            TableCellRenderer result = Columns.this.getColumn(modelColumn).cellRenderer;
            return result == null ? super.getCellRenderer(row, column) : result;
        }

        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
        {
            Component comp = super.prepareRenderer(renderer, row, column);

            if (!isRowSelected(row)) {
                if (row % 2 == 0) {
                    comp.setBackground(this.getBackground());
                } else {
                    comp.setBackground(oddRowColor);
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
}
