package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.ParametersPanel;

public interface Command<R>
{
    public String getTitle();

    public GroupParameter getParameters();

    public void go();
    
    public Results<R> getResults();
    
    public ParametersPanel createParametersPanel();

    public CommandPanel<R> createCommandPanel();
        
    public void defaultAction( R row );
    
    public void postCreate( CommandPanel<R> cp );
        
}
