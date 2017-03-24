package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.Tool;

public class HomeTask extends Task implements ListResults<Tool>
{
    public static List<Tool> results = new ArrayList<>();

    {
        results = new ArrayList<>();

        WrkF wrkFHome = new WrkF();
        wrkFHome.getTask().directory.setValueSafely(Resources.getInstance().getHomeDirectory());
        results.add(wrkFHome);

        WrkFTree wrkFTreeHome = new WrkFTree();
        wrkFTreeHome.getTask().directory.setValueSafely(Resources.getInstance().getHomeDirectory());
        wrkFTreeHome.getTask().depth.setValueSafely(1);
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
        places.task.store.setValueSafely(
            Util.createFile(Resources.getInstance().getHomeDirectory(), ".config", "gtk-3.0", "bookmarks"));
        results.add(places);

        PlacesChoices placesChoice = new PlacesChoices();
        placesChoice.directory
            .setValueSafely(new File(Resources.getInstance().getSettingsDirectory(), "placesChoices"));
        results.add(placesChoice);

        WrkOptionsFiles wrkOptionFiles = new WrkOptionsFiles();
        results.add(wrkOptionFiles);

        GroovyTools groovyTools = new GroovyTools();
        results.add(groovyTools);

        Terminal terminal = new Terminal();
        results.add(terminal);

        Terminal bash = new Terminal();
        bash.task.command.setValueSafely("bash\n--login");
        bash.title.setValueSafely("Bash");
        results.add(bash);
    }

    @Override
    public void body()
    {

    }

    @Override
    public List<Tool> getResults()
    {
        return new ArrayList<>(results);
    }
}
