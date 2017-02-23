package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.util.Util;

public abstract class Column<T>
{
    public String key;

    public String label;

    public abstract Object getValue(T row);

    public Column(String key)
    {
        this(key, Util.uncamel(key));
    }

    public Column(String key, String label)
    {
        this.key = key;
        this.label = label;
    }
}
