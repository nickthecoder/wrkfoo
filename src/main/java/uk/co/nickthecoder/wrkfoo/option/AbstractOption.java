package uk.co.nickthecoder.wrkfoo.option;


public abstract class AbstractOption implements Option
{
    private final String code;
    
    public String label;
    
    private boolean isRow;

        
    public AbstractOption( String code, String label, boolean isRow )
    {
        this.code = code;
        this.label = label;
        this.isRow = isRow;
    }

    public String getCode()
    {
        return code;
    }
    
    public String getLabel()
    {
        return label;
    }

    @Override
    public boolean isRow()
    {
        return isRow;
    }

    @Override
    public boolean isMultiRow()
    {
        return false;
    }
}
