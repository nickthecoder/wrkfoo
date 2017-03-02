package uk.co.nickthecoder.wrkfoo.option;

import uk.co.nickthecoder.wrkfoo.Command;

public interface Option
{    
    public String getCode();
    
    public String getLabel();

    public void runOption( Command<?> command, Object row, boolean newTab );
    
    public boolean isRow();
    
    public boolean isMultiRow();
}
