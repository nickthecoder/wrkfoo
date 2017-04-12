package uk.co.nickthecoder.wrkfoo;

import java.io.File;

public interface DirectoryTool<S extends Results> extends Tool<S>
{
    public File getDirectory();
    
    
}
