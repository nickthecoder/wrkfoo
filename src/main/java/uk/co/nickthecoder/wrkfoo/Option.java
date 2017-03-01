package uk.co.nickthecoder.wrkfoo;

public interface Option
{    
    public String getCode();
    
    public String getLabel();

    public void runOption( Command<?> command, Object row );
    
    public boolean isRow();
    
    public boolean isMultiRow();
}
