package uk.co.nickthecoder.wrkfoo;

public abstract class AbstractOption implements Option
{
    private final String code;
    
    public String label;
        
    public AbstractOption( String code, String label )
    {
        this.code = code;
        this.label = label;
    }

    public String getCode()
    {
        return code;
    }
    
    public String getLabel()
    {
        return label;
    }
}
