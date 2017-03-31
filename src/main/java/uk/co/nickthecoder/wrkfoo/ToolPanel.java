package uk.co.nickthecoder.wrkfoo;

import javax.swing.JComponent;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

public interface ToolPanel
{
    public JComponent getComponent();
    
    public void postCreate();

    public HidingSplitPane getSplitPane();

    public boolean check();

    public ParametersPanel getParametersPanel();

    public void go();

    public TopLevel getTopLevel();
 
    public void attachTo(ToolTab tab);

    public void detach();

    public ToolTab getToolTab();

}
