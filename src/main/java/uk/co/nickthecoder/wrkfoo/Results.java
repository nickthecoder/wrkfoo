package uk.co.nickthecoder.wrkfoo;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public interface Results<R>
{    
    public Columns<R> getColumns();
    
    public TableModel getTableModel();
        
    public JTable createTable();
    
    public R getRow(int row);
}
