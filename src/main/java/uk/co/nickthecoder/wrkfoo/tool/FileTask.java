package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

public abstract class FileTask extends GenericFileTask<RelativePath>
{
    @Override
    protected RelativePath parseLine(String line)
    {
        return new RelativePath(line) {
            public File getBase() {
                return directory.getValue();
            }
        };
    }

}
