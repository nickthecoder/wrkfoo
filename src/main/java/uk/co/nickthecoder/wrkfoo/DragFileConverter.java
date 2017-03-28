package uk.co.nickthecoder.wrkfoo;

import java.io.File;

import uk.co.nickthecoder.jguifier.guiutil.DragFileHandler;
import uk.co.nickthecoder.wrkfoo.tool.WrappedFile;

public class DragFileConverter<R extends WrappedFile> extends DragListConverter<R,File>
{
    public DragFileConverter()
    {
        super(DragFileHandler.FILE_LIST_FLAVORS);
    }

    @Override
    public File convertRow(WrappedFile row)
    {
        return row.file;
    }
}
