package uk.co.nickthecoder.wrkfoo.option;

import java.util.List;

import uk.co.nickthecoder.wrkfoo.Command;

public class NullOption implements Option
{
    public static final NullOption instance = new NullOption();
    
    private NullOption()
    {
    }

    @Override
    public String getCode()
    {
        return "";
    }

    @Override
    public String getLabel()
    {
        return "";
    }

    @Override
    public void runMultiOption(Command<?> command, List<Object> list, boolean newTab)
    {        
    }
    
    @Override
    public void runOption(Command<?> command, Object row, boolean newTab)
    {        
    }

    @Override
    public boolean isRow()
    {
        return false;
    }

    @Override
    public boolean isMultiRow()
    {
        return false;
    }

}
