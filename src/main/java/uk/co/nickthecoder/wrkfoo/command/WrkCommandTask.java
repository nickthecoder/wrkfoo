package uk.co.nickthecoder.wrkfoo.command;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.Command;

public class WrkCommandTask extends Task implements ListResults<Command<?>>
{
    public List<Command<?>> results;
    
    @Override
    public void body()
    {
        results = new ArrayList<Command<?>>();
        
        results.add( new WrkF() );
        results.add( new WrkFTree() );
    }

    @Override
    public List<Command<?>> getResults()
    {
        return results;
    }

}
