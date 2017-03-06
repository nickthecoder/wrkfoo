package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.guiutil.ScrollablePanel;
import uk.co.nickthecoder.jguifier.util.Stoppable;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.Option;
import uk.co.nickthecoder.wrkfoo.option.Options;
import uk.co.nickthecoder.wrkfoo.util.ToggleSplitPane;

public class CommandPanel<R> extends JPanel implements CommandListener
{
    protected Command<R> command;

    private ToggleSplitPane splitPane;

    private JPanel sidePanel;

    private ParametersPanel parametersPanel;

    private JPanel body;

    private JButton goButton;

    private JButton stopButton;

    protected SimpleTable<R> table;

    protected JScrollPane tableScrollPane;

    private JScrollPane parametersScrollPane;

    public CommandPanel(Command<R> foo)
    {
        this.command = foo;

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

        parametersScrollPane = new JScrollPane(scrollablePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
        goButton.setToolTipText("(Re)Run the command (F5)");
        goButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                go();
            }
        });
        goStop.add(goButton,gbc);

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
        goStop.add(stopButton,gbc);

        sidePanel.add(goStop, BorderLayout.SOUTH);

        table = command.createTable();
        table.setAutoCreateRowSorter(true);

        tableScrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        body.add(tableScrollPane, BorderLayout.CENTER);

        splitPane = new ToggleSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, body, sidePanel, false);
        splitPane.setResizeWeight(1);

        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);

        splitPane.toggle(false);
        if (this.command.getTask().getParameters().getChildren().size() == 0) {
            sidePanel.add(new JLabel("No Parameters"), BorderLayout.NORTH);
        }

        this.setBackground(Color.blue);
        this.command.addCommandListener(this);
    }

    public ToggleSplitPane getSplitPane()
    {
        return splitPane;
    }

    public ParametersPanel getParametersPanel()
    {
        return parametersPanel;
    }

    public void postCreate()
    {
        command.postCreate();

        MainWindow.putAction("F9", "toggleSidebar", this, new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                splitPane.toggle();
            }
        });

    }
    

    public void createNonRowOptionsMenu(MouseEvent me)
    {
        boolean useNewTab = me.isControlDown();

        JPopupMenu menu = new JPopupMenu();
        Options options = command.getOptions();
        for (Option option : options) {
            if (!option.isRow()) {
                menu.add(createOptionsMenuItem(option, -1, useNewTab));
            }
        }

        menu.show(me.getComponent(), me.getX(), me.getY());
    }

    protected JMenuItem createOptionsMenuItem(final Option option, final int rowIndex, final boolean useNewTab)
    {
        String extra = Util.empty(option.getCode()) ? "" : " (" + option.getCode() + ")";
        JMenuItem item = new JMenuItem(option.getLabel() + extra);
        item.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Object row = rowIndex >= 0 ? table.getModel().getRow(rowIndex) : null;
                if (option != null) {
                    option.runOption(command, row, useNewTab);
                }
            }
        });

        return item;
    }

    public void stopEditing()
    {
        if (table.isEditing()) {
            if (table.isEditing() && !table.getCellEditor().stopCellEditing()) {
                table.getCellEditor().cancelCellEditing();
            }
        }
    }

    public boolean check()
    {
        return parametersPanel.check(command.getTask());
    }

    public void go()
    {
        if (check()) {
            command.go();
        }
    }
    
    public void stop()
    {
        command.stop();
    }
    
    @Override
    public void changedState(boolean isRunning)
    {
        goButton.setEnabled(!isRunning);
        if ( command.getTask() instanceof Stoppable) {
            goButton.setVisible(!isRunning);
            stopButton.setVisible(isRunning);
        }
    }

}
