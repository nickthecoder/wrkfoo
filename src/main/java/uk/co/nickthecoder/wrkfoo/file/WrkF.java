package uk.co.nickthecoder.wrkfoo.file;

import java.io.File;

import uk.co.nickthecoder.wrkfoo.SimpleWrkFoo;

public class WrkF extends SimpleWrkFoo<WrkFTask,File>
{
    public static WrkFTask wrkFTask = new WrkFTask();
    
    public WrkF()
    {
        super( wrkFTask, "name", "lastModified", "size" );
    }

}
