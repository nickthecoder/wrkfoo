package uk.co.nickthecoder.wrkfoo;

import javax.swing.JPanel;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

public abstract class ToolPanel extends JPanel
{
    public abstract void postCreate();

    public abstract HidingSplitPane getSplitPane();

    public abstract boolean check();

    public abstract ParametersPanel getParametersPanel();

    public abstract void go();

}
