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
import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.TaskListener;
import uk.co.nickthecoder.jguifier.guiutil.WrapLayout;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.tool.ExportTableData;
import uk.co.nickthecoder.wrkfoo.tool.Home;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class ToolPanelToolBar implements TaskListener
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

        toolBar.add(builder.name("splitView").tooltip("Split View").buildButton());
        toolBar.add(builder.name("unsplitView").tooltip("Unsplit").buildButton());

        goButton = builder.name("run").tooltip("Re-Run the current tool").disable().buildButton();
        stopButton = builder.name("stop").tooltip("Stop current tool").hide().buildButton();
        toolBar.add(goButton);
        toolBar.add(stopButton);
        updateStopGoButtons(false);

        Task task = toolPanel.getTool().getTask();
        task.addTaskListener(this);
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

    private void updateStopGoButtons(final boolean running)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                int goState = running ? -1 : 0; // -1 Disabled Go, 0 = Go, 1 = Stop

                Task task = toolPanel.getTool().getTask();
                if (running && (task instanceof Stoppable)) {
                    goState = 1;
                }

                goButton.setVisible(goState != 1);
                stopButton.setVisible(goState == 1);
                goButton.setEnabled(goState >= 0);

            }
        });
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

    public void onSplitView()
    {
        Tab tab = toolPanel.getHalfTab().getTab();
        Tool<?> copiedTool = toolPanel.getTool().duplicate();
        
        tab.split(copiedTool);
    }

    public void onUnsplitView()
    {
        toolPanel.getHalfTab().getTab().unsplit();
    }

    @Override
    public void aborted(Task arg0)
    {
        updateStopGoButtons(false);
    }

    @Override
    public void ended(Task arg0, boolean arg1)
    {
        updateStopGoButtons(false);
    }

    @Override
    public void started(Task arg0)
    {
        updateStopGoButtons(true);
    }
}
