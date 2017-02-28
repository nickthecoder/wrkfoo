package uk.co.nickthecoder.wrkfoo.command;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;

public class NullTask extends Task implements ListResults<Object>
{
    public List<Object> results = new ArrayList<Object>(0);
    
    @Override
    public void body()
    {        
    }

    @Override
    public List<Object> getResults()
    {
        return results;
    }
}
