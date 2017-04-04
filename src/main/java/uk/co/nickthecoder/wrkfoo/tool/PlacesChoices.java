package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.io.PrintWriter;

import javax.swing.Icon;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.Resources;

/**
 * Like the {@link PlacesTool} tool, but instead of asking for a places store using a FileParameter,
 * this has a directory, where many places stores live, and you pick one from a pull down list.
 */
public class PlacesChoices extends PlacesTool
{
    public PlacesChoices()
    {
        super(new PlacesChoicesTask());
    }

    @Override
    public PlacesChoicesTask getTask()
    {
        return (PlacesChoicesTask) super.getTask();
    }

    @Override
    public Icon getIcon()
    {
        return Resources.icon("places.png");
    }

    @Override
    public String getTitle()
    {
        return "Places Choices";
    }

    /**
     * Used by the '+p' option
     * 
     * @return A task which will add a new the places file when run.
     */
    public AddPlacesTask addPlacesTask()
    {
        return new AddPlacesTask();
    }

    /**
     * Used by the '-p' option
     * 
     * @return A task which will remove the place files when run.
     */
    public RemovePlaceTask removePlacesTask()
    {
        return new RemovePlaceTask();
    }

    public class AddPlacesTask extends Task
    {
        public StringParameter name = new StringParameter.Builder("name")
            .parameter();

        public AddPlacesTask()
        {
            addParameters(name);
        }

        @Override
        public void body() throws Exception
        {
            File file = new File(getTask().directory.getValue(), name.getValue());
            // Create an empty file
            PrintWriter writer = new PrintWriter(file);
            writer.close();
            getTask().updateChoices();
        }
    }

    public class RemovePlaceTask extends Task
    {
        public RemovePlaceTask()
        {
        }

        @Override
        public void body() throws Exception
        {
            File file = getTask().store.getValue();
            file.delete();
            getTask().updateChoices();
        }
    }
}
