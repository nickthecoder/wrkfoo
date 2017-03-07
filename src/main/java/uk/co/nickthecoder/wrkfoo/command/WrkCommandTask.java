package uk.co.nickthecoder.wrkfoo.command;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.Command;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;

public class WrkCommandTask extends Task implements ListResults<Command>
{
    public List<Command> results;

    @Override
    public void body()
    {
        results = new ArrayList<Command>();

        WrkCommand wrkCommand = new WrkCommand();
        results.add(wrkCommand);

        WrkF wrkFHome = new WrkF();
        wrkFHome.getTask().directory.setValue(Resources.instance.getHomeDirectory());
        results.add(wrkFHome);

        WrkFTree wrkFTreeHome = new WrkFTree();
        wrkFTreeHome.getTask().directory.setValue(Resources.instance.getHomeDirectory());
        wrkFTreeHome.getTask().depth.setValue(1);
        results.add(wrkFTreeHome);

        WrkMounts wrkMountPoints = new WrkMounts();
        results.add(wrkMountPoints);

        ScanF scanF = new ScanF();
        results.add(scanF);

        GitStatus gitStatus = new GitStatus();
        results.add(gitStatus);

        WrkTabSets wrkTabSets = new WrkTabSets();
        results.add(wrkTabSets);

        Places places = new Places();
        places.task.store.setValue(Util.createFile(Resources.instance.getHomeDirectory(), ".config", "gtk-3.0", "bookmarks"));
        results.add(places);
    }

    @Override
    public List<Command> getResults()
    {
        return results;
    }

}
