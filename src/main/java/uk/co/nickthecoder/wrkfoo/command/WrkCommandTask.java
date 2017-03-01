package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.ListResults;

public class WrkCommandTask extends Task implements ListResults<Command<?>>
{
    public List<Command<?>> results;
    
    @Override
    public void body()
    {
        results = new ArrayList<Command<?>>();
        
        WrkF wrkFHome = new WrkF();
        wrkFHome.getTask().directory.setValue( new File( "/home/nick/3D" ) ); //new File( System.getProperty("user.home") ) );
        results.add( wrkFHome );
        
        WrkFTree wrkFTreeHome = new WrkFTree();
        wrkFTreeHome.getTask().directory.setValue( new File( "/home/nick/3D" ) ); //new File( System.getProperty("user.home") ) );
        wrkFTreeHome.getTask().depth.setValue( 1 );
        results.add( wrkFTreeHome );
    }

    @Override
    public List<Command<?>> getResults()
    {
        return results;
    }

}
