package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.wrkfoo.AbstractCommand;

public class WrkF extends AbstractCommand<WrkFTask, File>
{
    public WrkF()
    {
        super(new WrkFTask(), "name", "lastModified", "size");
    }

    @Override
    public ParametersPanel createParametersPanel()
    {
        ParametersPanel pp = new ParametersPanel();
        pp.addParameters(getTask().getParameters());
        return pp;

    }

}
