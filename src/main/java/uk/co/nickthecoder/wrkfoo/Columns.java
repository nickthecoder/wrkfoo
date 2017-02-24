package uk.co.nickthecoder.wrkfoo;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class Columns<R>
{
    private List<Column<R>> columns;

    private Color evenRowColor = new Color(230, 230, 230);

    public Columns()
    {
        columns = new ArrayList<Column<R>>();
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
        return new Table(tableModel);
    }

    public class Table extends JTable
    {
        public Table(TableModel model)
        {
            super(model);
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
                    comp.setBackground(evenRowColor);
                } else {
                    comp.setBackground(this.getBackground());
                }
            }
            return comp;

        }
    }
}
