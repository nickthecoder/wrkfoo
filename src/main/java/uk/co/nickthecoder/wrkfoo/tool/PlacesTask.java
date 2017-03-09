package uk.co.nickthecoder.wrkfoo.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.tool.PlacesTask.PlacesWrappedFile;

public class PlacesTask extends Task implements ListResults<PlacesWrappedFile>
{
    public FileParameter store = new FileParameter.Builder("store").
        file().mustExist().description("Text file containing a list of paths")
        .parameter();

    private File directory;

    public List<PlacesWrappedFile> results;

    public PlacesTask()
    {
        super();
        addParameters(store);
        try {
            store.setValue(new File(Resources.instance.getHomeDirectory(), ".places"));
        } catch (Exception e) {
            // Do nothing.
        }
    }

    @Override
    public List<PlacesWrappedFile> getResults()
    {
        return results;
    }

    @Override
    public void body()
    {
        directory = store.getValue().getParentFile();
        results = new ArrayList<PlacesWrappedFile>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(store.getValue())));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#")) {     
                    results.add(createWrappedFile(line));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

    }

    private PlacesWrappedFile createWrappedFile(String line)
        throws MalformedURLException
    {
        File file;
        String name = null;

        if (line.startsWith("file://")) {
            int space = line.indexOf(' ');
            URL url;
            if (space > 0) {
                url = new URL(line.substring(0, space));
                name = line.substring(space +1);
            } else {
                url = new URL(line);
            }
            try {
                file = new File(url.toURI());
            } catch (URISyntaxException e) {
                file = new File(url.getPath());
            }
        } else {
            file = new File(line);
        }
        if (name == null) {
            name = file.getName();
        }

        if (!file.isAbsolute()) {
            file = new File(directory, line);
        }
        return new PlacesWrappedFile(file, line, name);
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
