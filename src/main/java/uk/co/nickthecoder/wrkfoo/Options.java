package uk.co.nickthecoder.wrkfoo;

public interface Options
{
    public void add( Option option );

    public Option getDefault();
    
    public Option get( String code );
    
    public Option getUnsafe( String code );
    
    public boolean contains( String code );
}
