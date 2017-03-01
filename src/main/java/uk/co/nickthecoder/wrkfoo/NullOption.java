package uk.co.nickthecoder.wrkfoo;

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
    public void runOption(Command<?> command, Object row)
    {        
    }

}
