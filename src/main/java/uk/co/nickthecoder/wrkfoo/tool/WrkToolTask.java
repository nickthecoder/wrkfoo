package uk.co.nickthecoder.wrkfoo.tool;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.Tool;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;

public class WrkToolTask extends Task implements ListResults<Tool>
{
    public List<Tool> results;

    @Override
    public void body()
    {
        results = new ArrayList<Tool>();

        WrkTool wrkTool = new WrkTool();
        results.add(wrkTool);

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
    public List<Tool> getResults()
    {
        return results;
    }
}
