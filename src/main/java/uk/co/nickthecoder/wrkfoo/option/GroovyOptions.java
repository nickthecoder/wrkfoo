package uk.co.nickthecoder.wrkfoo.option;


public class GroovyOptions extends OptionsGroup
{
    private SimpleOptions simpleOptions;

    public GroovyOptions()
    {
        simpleOptions = new SimpleOptions();
        add(simpleOptions);
    }
    
    public void add( Option option )
    {
        simpleOptions.add( option );
    }

    @Override
    public void clear()
    {
        super.clear();
        simpleOptions.clear();
        add(simpleOptions);
    }
}
