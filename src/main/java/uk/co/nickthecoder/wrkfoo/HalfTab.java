package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

/**
 * A Tab can be split with two (displaying different tools in each half)
 * HalfTab holds the information for one half of this split, including the History
 */
public class HalfTab
{
    private Tab tab;

    private Tool<?> currentTool;

    private History history;

    private JPanel panel;

    public HalfTab(Tab tab, Tool<?> tool)
    {
        this.tab = tab;
        currentTool = tool;

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        history = new History();
    }

    public void postCreate()
    {
        ActionBuilder builder = new ActionBuilder(this).component(panel);

        builder.name("undoTool").buildShortcut();
        builder.name("redoTool").buildShortcut();
    }

    public Tab getTab()
    {
        return tab;
    }

    public Tool<?> getTool()
    {
        return currentTool;
    }

    public JPanel getPanel()
    {
        return panel;
    }

    final void attach(final Tool<?> tool)
    {
        currentTool = tool;
        panel.removeAll();
        panel.add(tool.getToolPanel().getComponent());
        tool.getToolPanel().attachTo(this);
    }

    void detach()
    {
        currentTool.getToolPanel().detach();
        Tool<?> tool = getTool();
        if (tool.getTask().isRunning()) {
            tool.stop();
        }
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

    /**
     * Used when the Tool's task's parameters have changed without the task being re-run by the normal
     * mechanism. Used by HTMLViewer for example when the address is changed.
     * This allows the normal history to work.
     */
    public void pushHistory()
    {
        history.add(currentTool);
    }

    public void go(Tool<?> newTool)
    {
        goPrivate(newTool, false, true);
    }

    public void goPrompt(Tool<?> newTool, boolean prompt)
    {
        goPrivate(newTool, prompt, true);
    }

    private void goPrivate(Tool<?> newTool, boolean prompt, boolean updateHistory)
    {
        if (newTool != currentTool) {
            detach();
            attach(newTool);
        }

        if (updateHistory) {
            history.add(newTool);
        }

        if (prompt) {

            newTool.getToolPanel().getSplitPane().showRight();

        } else {
            if (getTool().getToolPanel().check()) {
                // All parameters are ok, run the tool.

                newTool.go();

            } else {
                // Missing/incorrect parameters. Show the parameters panel.
                newTool.getToolPanel().getSplitPane().showRight();

            }
        }

        if (tab.getMainTabs() != null) {
            TabNotifier.fireChangedTitle(tab);
        }

        this.panel.repaint();
    }
}
