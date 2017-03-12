package uk.co.nickthecoder.wrkfoo.util;

import java.util.Date;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renders a date using just a short time, if the date is within the last 24 hours, otherwise renders the date,
 * without the time.
 */
public class DateRenderer extends DefaultTableCellRenderer
{
    private static final long serialVersionUID = 1L;

    public static final DateRenderer instance = new DateRenderer( CleverDateFormat.instance );
    
    private final CleverDateFormat format;
    
    public DateRenderer( CleverDateFormat format )
    {
        this.format = format;
    }
    
    @Override
    public void setValue(Object value)
    {
        if (value == null) {
            setText("");
            return;
        }

        setText(format.format((Date)value));
    }

    @Override
    public int getHorizontalAlignment()
    {
        return SwingConstants.CENTER;
    }
}
