package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import uk.co.nickthecoder.jguifier.guiutil.WithFile;
import uk.co.nickthecoder.wrkfoo.util.OSHelper;

public abstract class RelativePath implements WithFile
{
    final String path;
    private File file;

    public RelativePath(String path)
    {
        if (path.startsWith("./")) {
            this.path = path.substring(2);
        } else {
            this.path = path;
        }
    }

    public abstract File getBase();

    @Override
    public File getFile()
    {
        if (file == null) {
            file = new File(getBase(), path);
        }
        return file;
    }

    /**
     * Suitable for <code>row</code> to be passed to {@link OSHelper#command(String, Object...)}
     * 
     * @return The path
     */
    @Override
    public String toString()
    {
        return this.getFile().getPath();
    }
}