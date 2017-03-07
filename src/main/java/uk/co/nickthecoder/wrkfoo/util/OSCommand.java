package uk.co.nickthecoder.wrkfoo.util;

import java.util.Collection;

import uk.co.nickthecoder.jguifier.util.Exec;
import uk.co.nickthecoder.wrkfoo.Resources;
import uk.co.nickthecoder.wrkfoo.option.GroovyOption;
import uk.co.nickthecoder.wrkfoo.tool.ExecTool;

/**
 * A convenient way to run commands from a {@link GroovyOption}.
 * 
 * @see #tool(String, Object...)
 */
public class OSCommand
{
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
     * @return An {@link Exec}, which is NOT run by this method.
     */
    public static Exec command(String command, Object... args)
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
    
    public static ExecTool gui( String command, Object... args)
    {
        Exec exec = command( command, args ); 
        return new ExecTool(exec);
    }

    public static Exec edit(Object... args)
    {
        return command( Resources.instance.editor, args );
    }
    
    public static Exec openFolder(Object arg)
    {
        return command( Resources.instance.fileManager, arg );
    }
}

