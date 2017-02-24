package uk.co.nickthecoder.wrkfoo.util;

import java.lang.String;
import java.text.DecimalFormat;

public class SizeFormat
{
    public static final String units[] = { "", " KB", " MB", " GB", " TB" };

    public static final DecimalFormat format = new DecimalFormat("#.#");

    public static final SizeFormat instance = new SizeFormat();

    public String format(long sizel)
    {
        float size = (long) sizel;

        int power = 0;
        while (size > 1000) {
            power++;
            size = size / 1000;
            if (power >= units.length)
                break;
        }
        return format.format(size) + units[power];
    }
}
