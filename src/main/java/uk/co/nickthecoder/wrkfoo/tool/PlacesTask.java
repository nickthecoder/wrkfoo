package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.guiutil.BookmarkedPlaces;
import uk.co.nickthecoder.jguifier.guiutil.Places.Place;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;

public class PlacesTask extends Task implements ListResults<Place>
{
    public FileParameter store = new FileParameter.Builder("store").file().includeHidden().mustExist()
        .description("Text file containing a list of paths")
        .parameter();

    private File directory;

    public List<Place> results;

    public PlacesTask()
    {
        super();
        addParameters(store);
        try {
            store.setValue(new File(Resources.getInstance().getHomeDirectory(), ".places"));
        } catch (Exception e) {
            // Do nothing.
        }
    }

    @Override
    public List<Place> getResults()
    {
        return results;
    }

    @Override
    public void body()
    {
        directory = store.getValue().getParentFile();
        results = new ArrayList<>();

        results = new ArrayList<>();
        for (Place place : new BookmarkedPlaces(store.getValue(), "").getBookmarks()) {
            results.add(place);
        }
    }

    public class PlacesWrappedFile extends WrappedFile
    {
        public String line; // The whole line of the Places file.
        public String name; // The display name (which will be the filename if none was specified).

        public PlacesWrappedFile(File file, String line, String name)
        {
            super(file);
            this.line = line;
            this.name = name;
        }

        @Override
        public File getBase()
        {
            return directory;
        }
    }
}
