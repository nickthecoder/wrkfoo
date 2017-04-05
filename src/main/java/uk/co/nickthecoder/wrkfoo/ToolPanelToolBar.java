package uk.co.nickthecoder.wrkfoo;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import uk.co.nickthecoder.jguifier.guiutil.WrapLayout;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.tool.ExportTableData;
import uk.co.nickthecoder.wrkfoo.tool.Home;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class ToolPanelToolBar
{
    private ToolPanel toolPanel;

    /**
     * A panel, containing the statusBars and toolBars laid out vertically,
     */
    private JPanel toolAndStatusBars;

    /**
     * A set of JComponents laid out vertically, usually empty. (Editor adds the find status bar here)
     */
    private JPanel statusBars;

    /**
     * A set JToolBars laid out with WrapLayout. Initially contains {@link #toolBar}.
     */
    private JPanel toolBars;

    /**
     * The regular tool bar common to all tools. Contains {@link #optionTextField} and many JButtons.
     */
    private JToolBar toolBar;

    private JTextField optionTextField;

    public ToolPanelToolBar(ToolPanel toolPanel)
    {
        Util.assertIsEDT();

        this.toolPanel = toolPanel;

        toolAndStatusBars = new JPanel();
        toolAndStatusBars.setLayout(new BoxLayout(toolAndStatusBars, BoxLayout.Y_AXIS));

        toolBars = new JPanel();
        toolBars.setLayout(new WrapLayout(FlowLayout.LEFT));
        toolAndStatusBars.add(toolBars);

        statusBars = new JPanel();
        statusBars.setLayout(new BoxLayout(statusBars, BoxLayout.Y_AXIS));
        toolAndStatusBars.add(statusBars);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBars.add(toolBar);

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
        return toolAndStatusBars;
    }

    public JTextField getOptionsTextField()
    {
        return optionTextField;
    }

    public void addToolBar(JToolBar comp)
    {
        toolBars.add(comp);
    }

    public void addStatusBar(JComponent comp)
    {
        statusBars.add(comp);
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
