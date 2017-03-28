package uk.co.nickthecoder.wrkfoo;

import java.io.File;

import uk.co.nickthecoder.jguifier.guiutil.DragFileHandler;
import uk.co.nickthecoder.jguifier.guiutil.WithFile;

public class DragFileConverter<R extends WithFile> extends DragListConverter<R,File>
{
    public DragFileConverter()
    {
        super(DragFileHandler.FILE_LIST_FLAVORS);
    }

    @Override
    public File convertRow(WithFile row)
    {
        return row.getFile();
    }
}
