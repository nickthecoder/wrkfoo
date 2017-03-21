package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.nickthecoder.jguifier.util.Exec;

/**
 * Holds command data, which can then be run by feeding it into JediTerm (the terminal emulator), or into
 * {@link Exec}
 */
public class Command
{
    public List<String> command;

    public Map<String, String> env;

    public String directory;

    public Command(String... command)
    {
        this.command = new ArrayList<>();
        for (String s : command) {
            this.command.add(s);
        }
        env = new HashMap<>(System.getenv());        
    }

    public Command addArg(Object arg)
    {
        if (arg != null) {
            if (arg instanceof Collection<?>) {
                for (Object a : (Collection<?>) arg) {
                    addArg(a);
                }
            } else {
                this.command.add(arg.toString());
            }
        }
        return this;
    }

    public Command env(String name, String value)
    {
        env.put(name, value);
        return this;
    }

    public Command dir(File value)
    {
        return directory(value.getPath());
    }

    public Command directory(String value)
    {
        directory = value;
        return this;
    }

    /**
     * Looks in the PATH environment variable, and replaces the command name, with a fully path to the executable.
     * 
     * @return The absolute path to the executable or null if it was not found.
     */
    public static File which(String cmd)
    {
        File file = new File(cmd);

        if (file.isAbsolute()) {
            return file;
        }

        // Append to each part of the system PATH environment variable, looking for an executable.
        String pathString = System.getenv("PATH");
        String[] path = pathString.split(File.pathSeparator);
        for (String dir : path) {
            File exe = new File(new File(dir).getAbsolutePath(), cmd);
            if (exe.exists() && exe.canExecute()) {
                return exe;
            }
        }
        return null;
    }

    /**
     * Resolves the command, so that it is an absolute path to the executable
     * 
     * @return this
     * @throws FileNotFoundException
     */
    public Command resolve() throws FileNotFoundException
    {
        File file = which(command.get(0));
        if (file == null) {
            throw new FileNotFoundException();
        }
        command.set(0, file.getPath());

        return this;
    }

    public String[] getCommandArray()
    {
        String[] result = new String[command.size()];
        result = command.toArray(result);
        return result;
    }
}
