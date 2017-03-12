package uk.co.nickthecoder.wrkfoo.tool;

import java.io.File;

import javax.swing.Icon;

import uk.co.nickthecoder.wrkfoo.Resources;

public class Grep extends FileTool
{
    public static final Icon icon = Resources.icon("grep.png");

    public Grep(File directory)
    {
        super(new GrepTask(directory));
    }

    public Grep()
    {
        super(new GrepTask());
    }

    @Override
    public Icon getIcon()
    {
        return icon;
    }
}
