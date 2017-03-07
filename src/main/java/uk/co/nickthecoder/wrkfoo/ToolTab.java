package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import uk.co.nickthecoder.jguifier.Task;

public class ToolTab
{
    ToolTabbedPane tabbedPane;

    private MainWindow mainWindow;

    private Tool tool;

    private History history;

    private JPanel panel;

    public ToolTab(MainWindow mainWindow, Tool tool)
    {
        this.mainWindow = mainWindow;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        history = new History();
        attach(tool);
    }

    public String getTitle()
    {
        return tool.getShortTitle();
    }

    public JPanel getPanel()
    {
        return panel;
    }

    public MainWindow getMainWindow()
    {
        return mainWindow;
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

    private final void attach(Tool tool)
    {
        if (this.tool != null) {
            this.tool.detach();
        }

        this.tool = tool;
        tool.attachTo(this);
        panel.removeAll();
        panel.add(tool.getToolPanel());
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

            getTask().run();
            newTool.updateResults();

        } else {
            // Missing/incorrect parameters. Show the parameters panel.
            getTool().getToolPanel().getSplitPane().toggle(true);
        }

        if (tabbedPane != null) {
            tabbedPane.updateTabInfo(this);
        }

        this.panel.repaint();
    }
}