package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.Tool;

public class HomeTask extends Task implements ListResults<Tool<?>>
{
    public static List<Tool<?>> results = new ArrayList<>();

    static {
        results = new ArrayList<>();

        WrkF wrkFHome = new WrkF();
        wrkFHome.getTask().directory.setValueIgnoreErrors(Resources.getInstance().getHomeDirectory());
        results.add(wrkFHome);

        WrkFTree wrkFTreeHome = new WrkFTree();
        wrkFTreeHome.getTask().directory.setValueIgnoreErrors(Resources.getInstance().getHomeDirectory());
        wrkFTreeHome.getTask().depth.setValueIgnoreErrors(1);
        results.add(wrkFTreeHome);

        WrkMounts wrkMountPoints = new WrkMounts();
        results.add(wrkMountPoints);

        DiskUsage scanF = new DiskUsage();
        results.add(scanF);

        GitStatus gitStatus = new GitStatus();
        results.add(gitStatus);

        PlacesTool places = new PlacesTool();
        places.task.store.setValueIgnoreErrors(
            Util.createFile(Resources.getInstance().getHomeDirectory(), ".config", "gtk-3.0", "bookmarks"));
        results.add(places);

        PlacesChoices placesChoice = new PlacesChoices();
        placesChoice.getTask().directory
            .setValueIgnoreErrors(new File(Resources.getInstance().getSettingsDirectory(), "placesChoices"));
        results.add(placesChoice);

        WrkOptionsFiles wrkOptionFiles = new WrkOptionsFiles();
        results.add(wrkOptionFiles);

        GroovyTools groovyTools = new GroovyTools();
        results.add(groovyTools);

        Terminal terminal = new Terminal();
        results.add(terminal);

        Terminal bash = new Terminal();
        bash.task.command.setValue("bash");
        bash.task.arguments.addValue("--login");
        bash.task.title.setValueIgnoreErrors("Bash");
        results.add(bash);

        HTMLViewer htmlViewer = new HTMLViewer();
        results.add(htmlViewer);

        HTMLViewer about = new HTMLViewer();
        about.setTitle("About");
        about.task.address.setValue("http://nickthecoder.co.uk/wiki/view/software/WrkFoo");
        results.add(about);

    }

    @Override
    public void body()
    {

    }

    @Override
    public List<Tool<?>> getResults()
    {
        return new ArrayList<>(results);
    }
}
