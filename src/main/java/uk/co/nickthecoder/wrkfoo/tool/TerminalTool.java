package uk.co.nickthecoder.wrkfoo.tool;

import java.awt.BorderLayout;

import uk.co.nickthecoder.wrkfoo.AbstractTool;
import uk.co.nickthecoder.wrkfoo.ResultsPanel;

public class TerminalTool extends AbstractTool<TerminalTask>
{
    
    public TerminalTool()
    {
        super(new TerminalTask());
    }

    @Override
    public void updateResults()
    {       
        panel.removeAll();
        panel.add(task.termWidget, BorderLayout.CENTER);
        // TODO Put in a scroll pane
    }

    private ResultsPanel panel;
    
    @Override
    public ResultsPanel createResultsComponent()
    {
        panel = new ResultsPanel();
        panel.setLayout(new BorderLayout());        
        
        return panel;
    }

}
