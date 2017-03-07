package uk.co.nickthecoder.wrkfoo.tool;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.TextResults;

public class NullTask extends Task implements TextResults
{    
    @Override
    public void body()
    {        
    }

    @Override
    public String getResults()
    {
        return "";
    }
}
