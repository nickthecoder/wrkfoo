package uk.co.nickthecoder.wrkfoo.util;

import java.io.File;
import java.util.Comparator;

public class FoldersFirstComparator implements Comparator<File>
{
    public static final FoldersFirstComparator instance = new FoldersFirstComparator();

    private int nullValue = 1;
    
    public FoldersFirstComparator( boolean nullsFirst )
    {
        nullValue = nullsFirst ? -1 : 1;
    }
    
    public FoldersFirstComparator()
    {
        this(false);
    }
    
    @Override
    public int compare(File o1, File o2)
    {
        // Put nulls last
        if (o1 == null) {
            return o2 == null ? 0 : nullValue;
        }
        if (o2 == null) {
            return -nullValue;
        }

        // Neither are null

        if (o1.isDirectory() == o2.isDirectory()) {
            // Both are directories, or both are files
            return o1.compareTo(o2);
        }

        // One is a directory, the other is a file.
        return o1.isDirectory() ? -1 : 1;
    }

}