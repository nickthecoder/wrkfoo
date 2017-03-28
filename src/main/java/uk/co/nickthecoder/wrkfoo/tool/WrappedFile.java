package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import uk.co.nickthecoder.jguifier.guiutil.WithFile;

public class WrappedFile implements WithFile
{
    public final File file;

    public WrappedFile(File file)
    {
        this.file = file;
    }

    @Override
    public File getFile()
    {
        return file;
    }
    
    @Override
    public String toString()
    {
        return file.getPath();
    }

    public String getChoppedPath()
    {
        File base = getBase();
        if (base == null) {
            return file.getPath();
        }

        String path = file.getPath();
        String prefix = base.getPath();
        if ((path.startsWith(prefix)) && (path.length() > prefix.length())) {
            return path.substring(prefix.length() + 1);
        } else {
            return path;
        }
    }

    public File getBase()
    {
        return null;
    }
}
