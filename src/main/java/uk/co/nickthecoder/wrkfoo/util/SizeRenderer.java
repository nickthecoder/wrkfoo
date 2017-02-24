package uk.co.nickthecoder.wrkfoo.util;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

public class SizeRenderer extends DefaultTableCellRenderer
{
    public static final SizeRenderer instance = new SizeRenderer();

    @Override
    public void setValue(Object value)
    {
        if (value == null) {
            setText("");
            return;
        }

        setText(SizeFormat.instance.format((long) value));
    }

    @Override
    public int getHorizontalAlignment()
    {
        return JLabel.RIGHT;
    }
}
