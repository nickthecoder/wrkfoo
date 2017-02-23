package uk.co.nickthecoder.wrkfoo;

import java.util.Iterator;
import java.util.Map;

public interface Results<R>
{    
    public Iterator<R> rows();
    
    public Map<String,Column<R>> columnMap();
}
