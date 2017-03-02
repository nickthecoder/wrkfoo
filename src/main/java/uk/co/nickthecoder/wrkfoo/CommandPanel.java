package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.guiutil.ScrollablePanel;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.Option;
import uk.co.nickthecoder.wrkfoo.util.ToggleSplitPane;

public class CommandPanel<R> extends JPanel
{
    private Command<R> command;

    private ToggleSplitPane splitPane;

    private JPanel sidePanel;

    private ParametersPanel parametersPanel;

    private JPanel body;

    private JButton goButton;

    public SimpleTable<R> table;

    private JScrollPane tableScrollPane;

    private JScrollPane parametersScrollPane;

    public CommandPanel(Command<R> foo)
    {
        this.command = foo;

        sidePanel = new JPanel();
        body = new JPanel();
        body.setLayout(new BorderLayout());

        sidePanel.setLayout(new BorderLayout());
        parametersPanel = foo.createParametersPanel();

        ScrollablePanel scrollablePanel = new ScrollablePanel();
        scrollablePanel.setScrollableTracksViewportWidth(true);
        scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));
        scrollablePanel.add(parametersPanel);

        parametersScrollPane = new JScrollPane(scrollablePanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
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

        goButton = new JButton("Go");
        goButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                go();
            }
        });
        sidePanel.add(goButton, BorderLayout.SOUTH);

        table = command.createTable();
        table.setAutoCreateRowSorter(true);

        tableScrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        body.add(tableScrollPane, BorderLayout.CENTER);

        splitPane = new ToggleSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, body, sidePanel, false);
        splitPane.setResizeWeight(1);

        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);

        if (this.command.getTask().getParameters().getChildren().size() == 0) {
            splitPane.toggle(false);
            sidePanel.add(new JLabel("No Parameters"), BorderLayout.NORTH);
        }

        this.setBackground(Color.blue);
    }

    public ParametersPanel getParametersPanel()
    {
        return parametersPanel;
    }

    public JButton getGoButton()
    {
        return goButton;
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

        MainWindow.putAction("F5", "refresh", this, new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                go();
            }
        });

        MainWindow.putAction("ENTER", "defaultRowAction", table, JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
            new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    table.stopEditing();
                    CommandTableModel<?> model = table.getModel();
                    for (int i = 0; i < model.getRowCount(); i++) {
                        String code = model.getCode(i);
                        model.setCode(i, "");
                        if (!Util.empty(code)) {
                            Option option = command.getOptions().getRowOption(code);
                            if (option != null) {
                                option.runOption(command, model.getRow(i), false);
                            }
                        }
                    }
                }
            });

        MainWindow.putAction("ctrl ENTER", "defaultRowActionNewTab", table, JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
            new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    table.stopEditing();
                    CommandTableModel<?> model = table.getModel();
                    for (int i = 0; i < model.getRowCount(); i++) {
                        String code = model.getCode(i);
                        model.setCode(i, "");
                        if (!Util.empty(code)) {
                            Option option = command.getOptions().getRowOption(code);
                            if (option != null) {
                                option.runOption(command, model.getRow(i), true);
                            }
                        }
                    }
                }
            });

        table.addMouseListener(new MouseAdapter()
        {
            private int lastRowClicked = 0;

            public void mouseClicked(MouseEvent me)
            {
                if (me.isPopupTrigger()) {
                    createOptionsMenu();

                } else if (me.getClickCount() == 2) {
                    boolean newTab = me.isControlDown();
                    me.consume();
                    int r = table.getSelectedRow() >= 0 ?
                        table.convertRowIndexToModel(table.getSelectedRow()) :
                        lastRowClicked;

                    R row = table.getModel().getRow(r);
                    Option option = command.getOptions().getDefaultRowOption();
                    option.runOption(command, row, newTab);
                }
                if (table.getSelectedRow() >= 0) {
                    lastRowClicked = table.convertRowIndexToModel(table.getSelectedRow());
                }
            }
        });

    }

    private void createOptionsMenu()
    {

    }

    public void stopEditing()
    {
        if (table.isEditing()) {
            if (table.isEditing() && !table.getCellEditor().stopCellEditing()) {
                table.getCellEditor().cancelCellEditing();
            }
        }
    }

    public void go()
    {
        command.go();
    }

}
