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

    private MainWindow mainWindow;

    private Tool tool;

    private History history;

    private JPanel panel;

    private String titleTemplate = "%t";

    public ToolTab(MainWindow mainWindow, Tool tool)
    {
        this.mainWindow = mainWindow;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        history = new History();
        attach(tool);
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

    void setTabbedPane(TabbedPane value)
    {
        tabbedPane = value;
        if (value == null) {
            Tool tool = getTool();
            if (tool.isRunning()) {
                tool.stop();
            }
        }
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

    private final void attach(final Tool tool)
    {
        if (this.tool != null) {
            this.tool.detach();
        }

        this.tool = tool;
        tool.attachTo(this);
        panel.removeAll();
        panel.add(tool.getToolPanel());
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                tool.getToolPanel().getSplitPane().focus();
            }
        });

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
