package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import uk.co.nickthecoder.jguifier.FileParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.MainWindow;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.TabSetData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SaveTabSet extends Task
{
    public MainWindow mainWindow;
    
    public FileParameter fileParameter = new FileParameter.Builder("file")
        .writable().mayExist().file()
        .value(new File(Resources.instance.getTabsDirectory(), "new.json")).parameter();
    
    public SaveTabSet(MainWindow mainWindow)
    {
        this.mainWindow = mainWindow;
        addParameters( fileParameter );
    }

    @Override
    public void body()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        TabSetData tsd = new TabSetData( mainWindow );
        String json = gson.toJson(tsd);
        
        PrintWriter out = null;
        try {
            out = new PrintWriter( fileParameter.getValue() );
            out.println(json);
        } catch (FileNotFoundException e) {
            // TODO Report the error
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
    
}
