package uk.co.nickthecoder.wrkfoo;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.ParametersPanel;

public interface Command<R> extends Runnable
{
    public String getTitle();

    public GroupParameter getParameters();

    public void run();
    
    public Results<R> getResults();
    
    public ParametersPanel createParametersPanel();
        
}
