package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.BooleanParameter;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.MultipleParameter;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;

/**
 * There is no processing to be done, so TerminalTask has an empty {@link #body()}, all the processing is done
 * in {@link Terminal}.
 */
public class TerminalTask extends Task
{
    public StringParameter command = new StringParameter.Builder("command")
        .parameter();

    public MultipleParameter<StringParameter, String> arguments = new StringParameter.Builder("")
        .multipleParameter("arguments");

    public FileParameter directory = new FileParameter.Builder("directory").directory().includeHidden()
        .value(new File("."))
        .parameter();

    public BooleanParameter useSimpleTerminal = new BooleanParameter.Builder("useSimpleTerminal")
        .value(false)
        .parameter();

    public StringParameter title = new StringParameter.Builder("title")
        .value("Terminal")
        .parameter();

    public BooleanParameter autoClose = new BooleanParameter.Builder("autoClose")
        .parameter();

    public BooleanParameter killOnClose = new BooleanParameter.Builder("killOnClose")
        .value(true)
        .description("Kill the process when the tab is closed")
        .parameter();

    public TerminalTask(boolean hideCommand)
    {
        super();

        if (!hideCommand) {
            addParameters(command, arguments);
        }
        addParameters(directory, useSimpleTerminal, title, autoClose, killOnClose);
    }

    @Override
    public void body()
    {
    }

}
