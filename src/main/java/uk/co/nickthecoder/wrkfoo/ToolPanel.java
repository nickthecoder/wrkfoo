package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.guiutil.ScrollablePanel;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;
import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

public class ToolPanel extends JPanel implements ToolListener
{
    private static final long serialVersionUID = 1L;

    private Tool tool;

    private HidingSplitPane splitPane;

    private JPanel sidePanel;

    private ParametersPanel parametersPanel;

    private JPanel body;

    private JButton goButton;

    private JButton stopButton;

    private JScrollPane parametersScrollPane;

    protected ResultsPanel resultsPanel;

    public ToolPanel(Tool foo)
    {
        this.tool = foo;

        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(300, 300));
        body = new JPanel();
        body.setLayout(new BorderLayout());

        sidePanel.setLayout(new BorderLayout());
        parametersPanel = foo.createParametersPanel();

        ScrollablePanel scrollablePanel = new ScrollablePanel();
        scrollablePanel.setScrollableTracksViewportWidth(true);
        scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));
        scrollablePanel.add(parametersPanel);

        parametersScrollPane = new JScrollPane(scrollablePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        // parametersScrollPane = new
        // JScrollPane(parametersPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        parametersScrollPane.setMinimumSize(new Dimension(0, 0));

        // For some reason sharing the same color instance doesn't work. (And there is no copy, clone or copy
        // constructor), so lets copy the long way...
        Color srcColor = parametersPanel.getBackground();
        Color background = new Color(srcColor.getRed(), srcColor.getGreen(), srcColor.getBlue());
        parametersScrollPane.getViewport().setBackground(background);

        sidePanel.add(parametersScrollPane, BorderLayout.CENTER);

        JPanel goStop = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        goButton = new JButton("Go");
        goButton.setIcon(Resources.icon("run.png"));
        goButton.setToolTipText("(Re)Run the tool (F5)");
        goButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                go();
            }
        });
        goStop.add(goButton, gbc);

        stopButton = new JButton("Stop");
        stopButton.setIcon(Resources.icon("stop.png"));
        stopButton.setVisible(false);
        stopButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                stop();
            }
        });
        goStop.add(stopButton, gbc);

        sidePanel.add(goStop, BorderLayout.SOUTH);

        resultsPanel = tool.createResultsComponent();
        body.add(resultsPanel, BorderLayout.CENTER);

        splitPane = new HidingSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, body, sidePanel);
        splitPane.setResizeWeight(1);

        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);

        splitPane.setState(HidingSplitPane.State.LEFT);
        if (this.tool.getTask().getParameters().getChildren().size() == 0) {
            sidePanel.add(new JLabel("No Parameters"), BorderLayout.NORTH);
        }

        this.setBackground(Color.blue);
        this.tool.addToolListener(this);
    }

    public HidingSplitPane getSplitPane()
    {
        return splitPane;
    }

    public ResultsPanel getResultsPanel()
    {
        return resultsPanel;
    }

    public ParametersPanel getParametersPanel()
    {
        return parametersPanel;
    }

    public void postCreate()
    {
        tool.postCreate();

        ActionBuilder builder = new ActionBuilder(this);

        builder.name("toggleLeftPane").shortcut("F9").buildShortcut();
        builder.name("toggleRightPane").shortcut("ctrl F9").buildShortcut();
    }

    public void onToggleLeftPane()
    {
        MainWindow.focusLater("toogle left",  splitPane.toggleLeft(), 10);
    }

    public void onToggleRightPane()
    {
        MainWindow.focusLater("toogle right",  splitPane.toggleRight(), 10);
    }

    public boolean check()
    {
        return parametersPanel.check(tool.getTask());
    }

    public void go()
    {
        tool.getToolTab().go(tool);
    }

    public void stop()
    {
        tool.stop();
    }

    @Override
    public void changedState(final Tool tool)
    {
        WrkFoo.assertIsEDT();

        boolean isRunning = tool.isRunning();
        goButton.setEnabled(!isRunning);
        if (tool.getTask() instanceof Stoppable) {
            goButton.setVisible(!isRunning);
            stopButton.setVisible(isRunning);
        }

        MainWindow mainWindow = getMainWindow();
        if (mainWindow != null) {
            mainWindow.changedState(tool);
        }
    }

    public MainWindow getMainWindow()
    {
        return (MainWindow) SwingUtilities.getRoot(this);
    }

}
