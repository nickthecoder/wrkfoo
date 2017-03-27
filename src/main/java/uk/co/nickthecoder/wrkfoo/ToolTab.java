package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class ToolTab
{
    private TabbedPane tabbedPane;

    private Tool tool;

    private History history;

    private JPanel panel;

    private String titleTemplate = "%t";

    private boolean splitVertical;

    private String shortcut;

    public ToolTab(Tool tool)
    {
        this.tool = tool;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        history = new History();
        splitVertical = false;
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

    public boolean isSplitVertical()
    {
        return splitVertical;
    }

    public void setSplitVertical(boolean value)
    {
        this.splitVertical = value;
        this.getTool().getToolPanel().getSplitPane().setOrientation(
            splitVertical ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT);
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
        ActionBuilder builder = new ActionBuilder(this).component(panel);

        builder.name("undoTool").buildShortcut();
        builder.name("redoTool").buildShortcut();
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
        if (tool.getTask().isRunning()) {
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

    public void onUndoTool()
    {
        if (history.canUndo()) {
            goPrivate(history.undo(), false, false);
        }
    }

    public void onRedoTool()
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
            history.add(newTool);
        }

        if (prompt) {

            newTool.getToolPanel().getSplitPane().showRight();
            MainWindow.focusLater("Right - prompting new Tool",
                newTool.getToolPanel().getSplitPane().getRightComponent(), 8);

        } else {
            if (getTool().getToolPanel().check()) {
                // All parameters are ok, run the tool.

                newTool.go();

            } else {
                // Missing/incorrect parameters. Show the parameters panel.
                newTool.getToolPanel().getSplitPane().showRight();
                MainWindow.focusLater("Right - new tool has invalid parameters",
                    newTool.getToolPanel().getSplitPane().getRightComponent(), 8);
            }
        }

        if (tabbedPane != null) {
            tabbedPane.updateTabInfo(this);
        }

        this.panel.repaint();
    }
}
