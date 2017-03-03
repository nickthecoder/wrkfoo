package uk.co.nickthecoder.wrkfoo.util;

import java.io.File;

import javax.swing.table.DefaultTableCellRenderer;

import uk.co.nickthecoder.wrkfoo.command.WrkF;

/**
 * Renders a File using just its name.
 * This is needed, so that a JTable column can contain a File object, and therefore be ordered using a file comparator,
 * but rather than render it using its whole path, just render the name.
 * Used in {@link WrkF}
 */
public class FileNameRenderer extends DefaultTableCellRenderer
{
    public static final FileNameRenderer instance = new FileNameRenderer();

    @Override
    public void setValue(Object value)
    {
        if (value == null) {
            setText("");
            return;
        }

        setText(((File) value).getName());
    }
}
