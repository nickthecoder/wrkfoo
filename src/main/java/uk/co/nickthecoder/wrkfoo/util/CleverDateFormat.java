package uk.co.nickthecoder.wrkfoo.util;

import java.text.DateFormat;
import java.util.Date;

public class CleverDateFormat
{
    private static final long ONE_DAY = 1000 * 60 * 60 * 24;

    public static final CleverDateFormat instance = new CleverDateFormat();

    DateFormat dateFormat;

    DateFormat timeFormat;

    public CleverDateFormat()
    {
        this(DateFormat.getDateInstance(), DateFormat.getTimeInstance(DateFormat.SHORT));
    }

    public CleverDateFormat(DateFormat dateFormat, DateFormat timeFormat)
    {
        super();
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
    }

    public String format(Date date)
    {
        if (new Date().getTime() - date.getTime() < ONE_DAY) {
            return timeFormat.format(date);
        } else {
            return dateFormat.format(date);
        }
    }

}
