package uk.co.nickthecoder.wrkfoo.option;

public abstract class AbstractOption implements Option
{
    private final String code;

    public String label;

    private boolean isRow;

    private boolean isMultiRow;
    
    private boolean newTab;

    public AbstractOption(String code, String label, boolean isRow, boolean isMultiRow, boolean newTab)
    {
        this.code = code;
        this.label = label;
        this.isRow = isRow;
        this.isMultiRow = isMultiRow;
        this.newTab = newTab;
    }

    @Override
    public String getCode()
    {
        return code;
    }

    @Override
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
        return isMultiRow;
    }
    
    public boolean getNewTab()
    {
        return newTab;
    }
    
}
