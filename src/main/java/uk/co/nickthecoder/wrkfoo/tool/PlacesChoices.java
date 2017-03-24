package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.List;

import uk.co.nickthecoder.jguifier.ParameterListener;
import uk.co.nickthecoder.jguifier.parameter.ChoiceParameter;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.jguifier.util.FileLister;

/**
 * Like the {@link Places} tool, but instead of asking for a places store using a FileParameter,
 * this has a directory, where many places stores live, and you pick one from a pull down list.
 * 
 */
public class PlacesChoices extends Places
{
    public FileParameter directory = new FileParameter.Builder("directory").directory().mustExist()
        .parameter();

    public ChoiceParameter<File> store = new ChoiceParameter.Builder<File>("store")
        .parameter();

    public PlacesChoices()
    {
        super();
        updateChoices();

        directory.addListener(new ParameterListener()
        {

            @Override
            public void changed(Parameter source)
            {
                updateChoices();
            }
        });

        store.addListener(new ParameterListener()
        {

            @Override
            public void changed(Parameter source)
            {
                task.store.setValue(store.getValue());
            }
        });
        
        task.store.visible = false;
        task.addParameters(directory,store);
    }

    @Override
    public String getOptionsName()
    {
        return "places";
    }

    public void updateChoices()
    {
        store.clearChoices();

        if (directory.getValue() == null) {
            return;
        }
        
        FileLister lister = new FileLister();
        List<File> files = lister.listFiles(directory.getValue());

        for (File file : files) {
            store.addChoice(file.getName(), file);
        }
    }
}
