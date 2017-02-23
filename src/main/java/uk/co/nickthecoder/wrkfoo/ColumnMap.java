package uk.co.nickthecoder.wrkfoo;

import java.util.HashMap;

public class ColumnMap<R> extends HashMap<String,Column<R>>
{
    private static final long serialVersionUID = 1L;

    public void add( Column<R> column )
    {
        put( column.key, column );
    }
}
