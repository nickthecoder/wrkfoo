package uk.co.nickthecoder.wrkfoo.util;

import java.nio.file.Path;

public interface FileListener
{
    public void fileChanged(Path path);
}
