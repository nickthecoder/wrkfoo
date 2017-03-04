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

public class PlacesTask extends Task implements ListResults<File>
{
    public FileParameter store = new FileParameter.Builder("store").
        file().mustExist().description("Text file containing a list of paths")
        .parameter();

    public List<File> results;

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
    public List<File> getResults()
    {
        return results;
    }

    @Override
    public void body()
    {
        results = new ArrayList<File>();

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
                results.add(file);
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

}
