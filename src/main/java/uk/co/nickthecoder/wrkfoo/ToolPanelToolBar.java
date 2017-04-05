package uk.co.nickthecoder.wrkfoo;

import javax.swing.JComponent;
import javax.swing.JToolBar;

import uk.co.nickthecoder.wrkfoo.tool.ExportTableData;
import uk.co.nickthecoder.wrkfoo.tool.Home;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class ToolPanelToolBar
{
    private ToolPanel toolPanel;

    private JToolBar toolBar;

    public ToolPanelToolBar(ToolPanel toolPanel)
    {
        this.toolPanel = toolPanel;

        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        ActionBuilder builder = new ActionBuilder(this).component(toolPanel.getComponent());

        toolBar.add(builder.name("home").tooltip("Home : Show all Tools").buildButton());
        toolBar.add(builder.name("back").tooltip("Go back through the tool history").buildButton());
        toolBar.add(builder.name("forward").tooltip("Go forward through the tool history").buildButton());
        toolBar.add(builder.name("exportTable").tooltip("Export Table Data").buildButton());

    }

    public JComponent getComponent()
    {
        return toolBar;
    }

    public void onHome()
    {
        Home tool = new Home();
        toolPanel.getToolTab().go(tool);
    }

    public void onBack()
    {
        toolPanel.getToolTab().onUndoTool();
    }

    public void onForward()
    {
        toolPanel.getToolTab().onRedoTool();
    }

    public void onExportTable()
    {
        Tool<?> tool = toolPanel.getToolTab().getTool();
        if (tool instanceof TableTool<?, ?>) {
            ExportTableData std = new ExportTableData((TableTool<?, ?>) tool);
            std.promptTask();
        }
    }
}
