package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.util.List;

import uk.co.nickthecoder.jguifier.ParameterListener;
import uk.co.nickthecoder.jguifier.parameter.ChoiceParameter;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.jguifier.util.FileLister;

public class PlacesChoicesTask extends PlacesTask
{
    public FileParameter directory = new FileParameter.Builder("directory").directory().mustExist()
        .parameter();

    public ChoiceParameter<File> storeChoices = new ChoiceParameter.Builder<File>("store")
        .parameter();

    public PlacesChoicesTask()
    {
        super();

        directory.addListener(new ParameterListener()
        {

            @Override
            public void changed(Parameter source)
            {
                updateChoices();
            }
        });

        storeChoices.addListener(new ParameterListener()
        {
            @Override
            public void changed(Parameter source)
            {
                if (storeChoices.getValue() != null) {
                    store.setValueIgnoreErrors(storeChoices.getValue());
                }
            }
        });

        store.visible = false;
        addParameters(directory, storeChoices);
    }

    public void updateChoices()
    {
        storeChoices.clearChoices();

        if (directory.getValue() == null) {
            return;
        }

        FileLister lister = new FileLister();
        List<File> files = lister.listFiles(directory.getValue());

        for (File file : files) {
            storeChoices.addChoice(file.getName(), file);
        }
    }
}
