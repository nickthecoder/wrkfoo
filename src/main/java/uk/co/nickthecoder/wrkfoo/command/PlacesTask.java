package uk.co.nickthecoder.wrkfoo.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.command.PlacesTask.PlacesWrappedFile;

public class PlacesTask extends Task implements ListResults<PlacesWrappedFile>
{
    public FileParameter store = new FileParameter.Builder("store").
        file().mustExist().description("Text file containing a list of paths")
        .parameter();

    public File directory;
    
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
            File directory = store.getValue().getParentFile();
            
            String line;
            while ((line = reader.readLine()) != null) {
                File file = new File(line);
                if (!file.isAbsolute()) {
                    file = new File( directory, line );
                }
                results.add(new PlacesWrappedFile(file));
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

    public class PlacesWrappedFile extends WrappedFile
    {

        public PlacesWrappedFile(File file)
        {
            super(file);
            // TODO Auto-generated constructor stub
        }
        
        public File getBase()
        {
            return directory;
        }
    }
}
