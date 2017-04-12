package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import uk.co.nickthecoder.jguifier.ParameterListener;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.wrkfoo.util.DirectoryListener;
import uk.co.nickthecoder.wrkfoo.util.DirectoryWatcher;

public abstract class WatchDirectoryParameter implements ParameterListener, DirectoryListener
{
    private FileParameter directoryParameter;

    private Path registeredPath;

    public WatchDirectoryParameter(FileParameter directoryParameter)
    {
        this.directoryParameter = directoryParameter;

        directoryParameter.addListener(this);
        File dir = directoryParameter.getValue();
        if (dir != null) {
            register(dir.toPath());
        }
    }

    public void remove()
    {
        directoryParameter.remvoveListener(this);
        unregister();
    }

    private void register(Path path)
    {
        registeredPath = path;
        try {
            DirectoryWatcher.getInstance().register(registeredPath, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unregister()
    {
        if (registeredPath != null) {
            DirectoryWatcher.getInstance().unregister(registeredPath, this);
            registeredPath = null;
        }
    }

    @Override
    public void changed(Object initiator, Parameter source)
    {
        File dir = directoryParameter.getValue();
        if (dir == null) {
            unregister();
        } else {
            Path path = dir.toPath();
            if (path.equals(registeredPath)) {
                return;
            } else {
                if (registeredPath != null) {
                    unregister();
                    register(path);
                }
            }
        }
    }

    @Override
    public abstract void directoryChanged(Path directory, Path file);

}
