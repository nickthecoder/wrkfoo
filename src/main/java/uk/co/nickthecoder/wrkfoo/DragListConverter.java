package uk.co.nickthecoder.wrkfoo;

import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.guiutil.DragListHandler;
import uk.co.nickthecoder.jguifier.guiutil.DragListListener;

public abstract class DragListConverter<R, T> implements DragListListener<T>
{
    private DataFlavor[] flavors;
    
    private SimpleTable<R> table;

    public DragListConverter(DataFlavor[] flavors)
    {
        this.flavors = flavors;
    }

    public void createDragListHandler(SimpleTable<R> table)
    {
        this.table = table;
        new DragListHandler<T>(this, flavors).draggable(table);
    }
    
    public abstract T convertRow( R row );
    
    @Override
    public List<T> getDragList()
    {
        List<T> results = new ArrayList<>();

        for (int tr : table.getSelectedRows()) {
            int mr = table.convertRowIndexToModel(tr);
            R row = table.getModel().getRow(mr);
            results.add(convertRow(row));
        }

        return results;
    }

}
