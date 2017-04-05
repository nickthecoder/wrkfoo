package uk.co.nickthecoder.wrkfoo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import uk.co.nickthecoder.wrkfoo.tool.ExportTableData;
import uk.co.nickthecoder.wrkfoo.tool.Home;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class ToolPanelToolBar
{
    private ToolPanel toolPanel;

    private JToolBar toolBar;

    private JTextField optionTextField;

    public ToolPanelToolBar(ToolPanel toolPanel)
    {
        this.toolPanel = toolPanel;

        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        optionTextField = createOptionTextField();
        toolBar.add(optionTextField);

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

    public JTextField getOptionsTextField()
    {
        return optionTextField;
    }

    private JTextField createOptionTextField()
    {
        JTextField textField = new JTextField();
        textField.setToolTipText("Enter non-row Options (F10)");
        textField.setColumns(6);

        ActionBuilder builder = new ActionBuilder(this).component(textField);

        builder.name("promptNonRowOption").buildShortcut();
        builder.name("promptNonRowOptionInNewTab").buildShortcut();

        builder.name("runNonRowOptionInNewTab").buildShortcut();
        builder.name("runNonRowOption").buildShortcut();

        builder.name("jumpToNonRowOption").buildShortcut();

        textField.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseReleased(MouseEvent me)
            {
                if (me.isPopupTrigger()) {
                    createOptionsMenu(me);
                }
            }

            @Override
            public void mousePressed(MouseEvent me)
            {
                if (me.isPopupTrigger()) {
                    createOptionsMenu(me);
                }
            }
        });

        return textField;
    }

    private void createOptionsMenu(MouseEvent me)
    {
        Tool<?> tool = toolPanel.getToolTab().getTool();
        new OptionsRunner(tool).popupNonRowMenu(me);
    }

    private void processOptionField(boolean newTab, boolean prompt)
    {
        Tool<?> tool = toolPanel.getToolTab().getTool();

        if (!optionTextField.getText().equals("")) {
            if (new OptionsRunner(tool).runOption(optionTextField.getText(), newTab, prompt)) {
                optionTextField.setText("");
            }
        }
    }

    public void onRunNonRowOption()
    {
        processOptionField(false, false);
    }

    public void onRunNonRowOptionInNewTab()
    {
        processOptionField(true, false);
    }

    public void onPromptNonRowOption()
    {
        processOptionField(false, true);
    }

    public void onPromptNonRowOptionInNewTab()
    {
        processOptionField(true, true);
    }

    public void onJumpToNonRowOption()
    {
        Focuser.focusLater("MainWindow.JumptToToolBar", optionTextField, 8);
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
