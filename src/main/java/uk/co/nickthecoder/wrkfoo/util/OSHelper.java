package uk.co.nickthecoder.wrkfoo.util;

import java.io.File;
import java.util.Collection;

import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.GroovyOption;
import uk.co.nickthecoder.wrkfoo.tool.ExecTool;

/**
 * A convenient way to run operating system commands from a {@link GroovyOption}.
 * GroovyOption places an instance of {@link OSHelper} into its bindings, with the name <code>os</code>,
 * so for example, an option could contain the following action : <code>os.run('echo' 'Hello world')</code>
 */
public class OSHelper
{
    public static OSHelper instance = new OSHelper();

    /**
     * Prepares (but does not run) an {@link Exec}, with the given tool and arguments.
     * <p>
     * Any null arguments are stripped out. If one of <code>args</code> is a {@link Collection}, then each item in the
     * collection is sent as a separate argument (again, nulls are stripped out).
     * </p>
     * 
     * @param tool
     *            The name of the tool to run
     * @param args
     *            The arguments for the tool.
     * @return An {@link Exec}, which is NOT run by this method ({@link GroovyOption} will run it).
     */
    public Exec exec(String command, Object... args)
    {
        Exec result = new Exec(command);
        for (Object arg : args) {
            if (arg != null) {
                if (arg instanceof Collection<?>) {
                    for (Object item : ((Collection<?>) arg)) {
                        if (item != null) {
                            result.add(item.toString());
                        }
                    }
                } else {
                    result.add(arg.toString());
                }
            }
        }
        return result;
    }

    /**
     * Shows the output of the command in a wrkfoo tab.
     * Note that this method doesn't run the command, GroovyOption will
     * 
     * @param command
     *            The operating system command
     * @param args
     *            The set of command line argument passes to <code>command</code>. See {@link #exec(String, Object...)}.
     * @return An ExecTool which GroovyOption will run.
     */
    public ExecTool show(String command, Object... args)
    {
        Exec exec = exec(command, args);
        return new ExecTool(exec);
    }

    /**
     * Starts a text editor. You can choose which text editor to use, from wrkfoo's settings.json - See
     * {@link Resources#editor}.
     * 
     * @param args
     *            A list of the files to open. See {@link #exec(String, Object...)}.
     * @return An Exec which GroovyOption will run.
     */
    public Exec edit(Object... args)
    {
        return exec(Resources.getInstance().getEditor(), args);
    }

    /**
     * Opens the a directory in a file manager. You can choose which file manager to use in wrkfoo's settings.json. See
     * {@link Resources#fileManager}.
     * 
     * @param arg
     *            The file to open (can be a {@link File}, or a String path. See {@link #exec(String, Object...)}.
     * @return An Exec which GroovyOption will run.
     */
    public Exec openFolder(Object arg)
    {
        return exec(Resources.getInstance().getFileManager(), arg);
    }
}
