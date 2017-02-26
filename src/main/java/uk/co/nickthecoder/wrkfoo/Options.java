package uk.co.nickthecoder.wrkfoo;

public interface Options
{
    public void add( String shortcut, Option option );

    public Option getDefault();
    
    public Option get( String shortcut );
    
}
