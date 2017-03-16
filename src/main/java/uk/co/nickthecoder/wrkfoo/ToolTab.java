package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Util;

public class ToolTab
{
    private TabbedPane tabbedPane;

    private Tool tool;

    private History history;

    private JPanel panel;

    private String titleTemplate = "%t";

    private String shortcut;

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

    public String getShortcut()
    {
        return shortcut;
    }

    public void setShortcut(String value)
    {
        if (Util.equals(value, shortcut)) {
            return;
        }

        InputMap inputMap = getTabbedPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getTabbedPane().getActionMap();

        if (shortcut != null) {
            String actionMapKey = "selectToolTab-" + shortcut;
            KeyStroke keyStroke = KeyStroke.getKeyStroke(shortcut);
            inputMap.remove(keyStroke);
            actionMap.remove(actionMapKey);
        }

        shortcut = value;
        String actionMapKey = "selectToolTab-" + shortcut;

        if (!Util.empty(shortcut)) {

            KeyStroke keyStroke = KeyStroke.getKeyStroke(shortcut);
            inputMap.put(keyStroke, actionMapKey);
            actionMap.put(actionMapKey, new AbstractAction()
            {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    getTabbedPane().setSelectedToolTab(ToolTab.this);
                }
            });
        }
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
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                undo();
            }
        });

        MainWindow.putAction("alt RIGHT", "Action.redo", panel, new AbstractAction()
        {
            private static final long serialVersionUID = 1L;

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
            goPrivate(history.undo(), false, false);
        }
    }

    public void redo()
    {
        if (history.canRedo()) {
            goPrivate(history.redo(), false, false);
        }
    }

    public void go(Tool newTool)
    {
        goPrivate(newTool, false, true);
    }

    public void goPrompt(Tool newTool, boolean prompt)
    {
        goPrivate(newTool, prompt, true);
    }

    private void goPrivate(Tool newTool, boolean prompt, boolean updateHistory)
    {
        if (newTool != this.tool) {
            detach();
            attach(newTool);
        }

        if (updateHistory) {
            history.add(tool);
        }

        if (prompt) {

            tool.getToolPanel().getSplitPane().focusRight();

        } else {
            if (getTool().getToolPanel().check()) {
                // All parameters are ok, run the tool.

                newTool.go();

            } else {
                // Missing/incorrect parameters. Show the parameters panel.
                getTool().getToolPanel().getSplitPane().showRight();
            }
        }

        if (tabbedPane != null) {
            tabbedPane.updateTabInfo(this);
        }

        this.panel.repaint();
    }
}
