package uk.co.nickthecoder.wrkfoo.util;

import java.nio.file.Path;

public interface DirectoryListener
{
    public void directoryChanged(Path directory, Path file);
}
