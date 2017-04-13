package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import uk.co.nickthecoder.jguifier.ParameterListener;
import uk.co.nickthecoder.jguifier.parameter.FileParameter;
import uk.co.nickthecoder.jguifier.parameter.Parameter;
import uk.co.nickthecoder.wrkfoo.util.FileListener;
import uk.co.nickthecoder.wrkfoo.util.FileWatcher;

public abstract class WatchFileParameter implements ParameterListener, FileListener
{
    private FileParameter fileParameter;

    private Path registeredPath;

    public WatchFileParameter(FileParameter fileParameter)
    {
        this.fileParameter = fileParameter;

        fileParameter.addListener(this);
        File dir = fileParameter.getValue();
        if (dir != null) {
            register(dir.toPath());
        }
    }

    public void remove()
    {
        fileParameter.remvoveListener(this);
        unregister();
    }

    private void register(Path path)
    {
        registeredPath = path;
        try {
            FileWatcher.getInstance().register(registeredPath, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unregister()
    {
        if (registeredPath != null) {
            FileWatcher.getInstance().unregister(registeredPath, this);
            registeredPath = null;
        }
    }

    @Override
    public void changed(Object initiator, Parameter source)
    {       
        File file = fileParameter.getValue();
        if (file == null) {
            unregister();
        } else {
            Path path = file.toPath();
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
    public abstract void fileChanged(Path path);

}
