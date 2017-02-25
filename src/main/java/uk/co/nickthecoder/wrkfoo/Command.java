package uk.co.nickthecoder.wrkfoo;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.ParametersPanel;

public interface Command<R>
{
    public void postCreate( CommandPanel<R> cp );

    
    public String getTitle();

    public Icon getIcon();

    public GroupParameter getParameters();

    
    public ParametersPanel createParametersPanel();
    
    public CommandPanel<R> getCommandPanel();
        
    public void defaultAction( R row );
    
    
    
    public void go();
    
    public Results<R> getResults();
    
}
