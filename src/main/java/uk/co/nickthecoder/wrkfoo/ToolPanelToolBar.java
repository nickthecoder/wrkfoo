package uk.co.nickthecoder.wrkfoo;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.guiutil.WrapLayout;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.tool.ExportTableData;
import uk.co.nickthecoder.wrkfoo.tool.Home;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.AutoComponentUpdater;

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

    private JButton goButton;

    private JButton stopButton;

    @SuppressWarnings("unused") // Keep a reference to stop it being garbage collected.
    private AutoComponentUpdater componentUpdater;

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

        goButton = builder.name("run").tooltip("Re-Run the current tool").disable().buildButton();
        stopButton = builder.name("stop").tooltip("Stop current tool").hide().buildButton();
        toolBar.add(goButton);
        toolBar.add(stopButton);
        updateStopGoButtons();

        final JButton back;
        final JButton forwards;

        toolBar.add(builder.name("home").tooltip("Home : Show all Tools").buildButton());
        toolBar.add(back = builder.name("back").tooltip("Go back through the tool history").buildButton());
        toolBar.add(forwards = builder.name("forward").tooltip("Go forward through the tool history").buildButton());
        if (toolPanel.getTool() instanceof TableTool) {
            toolBar.add(builder.name("exportTable").tooltip("Export Table Data").buildButton());
        }

        toolBar.add(builder.name("closeHalfTab").tooltip("Close").buildButton());

        componentUpdater = new AutoComponentUpdater(
            getClass().getSimpleName() + " for " + toolPanel.getTool().getClass().getSimpleName())
        {
            @Override
            public void autoUpdate()
            {
                back.setEnabled(toolPanel.getHalfTab().getHistory().canUndo());
                forwards.setEnabled(toolPanel.getHalfTab().getHistory().canRedo());

                updateStopGoButtons();
            }
        };
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

    private void updateStopGoButtons()
    {
        Util.assertIsEDT();

        boolean running = toolPanel.getTool().getTask().isRunning();
        int goState = running ? -1 : 0; // -1 Disabled Go, 0 = Go, 1 = Stop

        Task task = toolPanel.getTool().getTask();
        if (running && (task instanceof Stoppable)) {
            goState = 1;
        }

        goButton.setVisible(goState != 1);
        stopButton.setVisible(goState == 1);
        goButton.setEnabled(goState >= 0);
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
        Tool<?> tool = toolPanel.getTool();
        new OptionsRunner(tool).popupNonRowMenu(me);
    }

    private void processOptionField(boolean newTab, boolean prompt)
    {
        Tool<?> tool = toolPanel.getTool();

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
        toolPanel.getHalfTab().go(tool);
    }

    public void onBack()
    {
        toolPanel.getHalfTab().onUndoTool();
    }

    public void onForward()
    {
        toolPanel.getHalfTab().onRedoTool();
    }

    public void onExportTable()
    {
        Tool<?> tool = toolPanel.getTool();
        if (tool instanceof TableTool<?, ?>) {
            ExportTableData std = new ExportTableData((TableTool<?, ?>) tool);
            std.promptTask();
        }
    }

    public void onRun()
    {
        toolPanel.getTool().getToolPanel().go();
    }

    public void onStop()
    {
        Tool<?> tool = toolPanel.getTool();
        if (tool instanceof Stoppable) {
            Stoppable s = (Stoppable) tool;
            s.stop();
        }
    }

    public void onCloseHalfTab()
    {
        Tab tab = toolPanel.getHalfTab().getTab();
        if (tab.isSplit()) {
            tab.unsplit(toolPanel.getHalfTab());
        } else {
            tab.getMainTabs().removeTab(tab);
        }
    }
}
