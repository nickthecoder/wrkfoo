package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.Task;

public class ToolTab
{
    private TabbedPane tabbedPane;

    private Tool tool;

    private History history;

    private JPanel panel;

    private String titleTemplate = "%t";

    public ToolTab(Tool tool)
    {
        this.tool = tool;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        history = new History();
    }

    public void setTitleTemplate(String value)
    {
        titleTemplate = value;
    }

    public String getTitleTemplate()
    {
        return titleTemplate;
    }

    public String getTitle()
    {
        return titleTemplate.replaceAll("%t", tool.getShortTitle());
    }

    public JPanel getPanel()
    {
        return panel;
    }

    public TabbedPane getTabbedPane()
    {
        return tabbedPane;
    }

    public void postCreate()
    {
        MainWindow.putAction("alt LEFT", "Action.undo", panel, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                undo();
            }
        });

        MainWindow.putAction("alt RIGHT", "Action.redo", panel, new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                redo();
            }
        });
    }

    void setTabbedPane(TabbedPane value)
    {
        if (value == null) {
            detach();
        }
        tabbedPane = value;
        if (value != null) {
            attach(tool);
        }
    }
    
    private final void attach(final Tool tool)
    {
        this.tool = tool;
        panel.removeAll();
        panel.add(tool.getToolPanel());
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                tool.getToolPanel().getSplitPane().focus();
            }
        });
        tool.attachTo(this);

    }

    private void detach()
    {
        this.tool.detach();
        Tool tool = getTool();
        if (tool.isRunning()) {
            tool.stop();
        }
    }

    public Tool getTool()
    {
        return tool;
    }

    public Task getTask()
    {
        return tool.getTask();
    }

    public void undo()
    {
        if (history.canUndo()) {
            go(history.undo(), false);
        }
    }

    public void redo()
    {
        if (history.canRedo()) {
            go(history.redo(), false);
        }
    }

    public void go(Tool newTool)
    {
        go(newTool, true);
    }

    private void go(Tool newTool, boolean updateHistory)
    {
        if (newTool != this.tool) {
            attach(newTool);
        }

        if (updateHistory) {
            history.add(tool);
        }

        if (getTool().getToolPanel().check()) {
            // All parameters are ok, run the tool.

            newTool.go();

        } else {
            // Missing/incorrect parameters. Show the parameters panel.
            getTool().getToolPanel().getSplitPane().showRight();
        }

        if (tabbedPane != null) {
            tabbedPane.updateTabInfo(this);
        }

        this.panel.repaint();
    }
}
