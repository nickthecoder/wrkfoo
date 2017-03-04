package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.jguifier.guiutil.ScrollablePanel;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.Option;
import uk.co.nickthecoder.wrkfoo.option.Options;
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
        sidePanel.setPreferredSize(new Dimension(300,300));
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

        splitPane.toggle(false);
        if (this.command.getTask().getParameters().getChildren().size() == 0) {
            sidePanel.add(new JLabel("No Parameters"), BorderLayout.NORTH);
        }

        this.setBackground(Color.blue);
    }
    
    public ToggleSplitPane getSplitPane()
    {
        return splitPane;
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

        MainWindow.putAction("ENTER", "defaultRowAction", table, JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
            new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    processOptions(false);
                }
            });

        MainWindow.putAction("ctrl ENTER", "defaultRowActionNewTab", table, JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
            new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    processOptions(true);
                }
            });

        table.addMouseListener(new MouseAdapter()
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

            @Override
            public void mouseClicked(MouseEvent me)
            {
                if (me.getClickCount() == 2) {
                    boolean newTab = me.isControlDown();
                    me.consume();
                    int rowIndex = table.convertRowIndexToModel(table.rowAtPoint(me.getPoint()));

                    R row = table.getModel().getRow(rowIndex);
                    Option option = command.getOptions().getDefaultRowOption();
                    option.runOption(command, row, newTab);
                }

            }
        });

    }
    
    private void processOptions(boolean newTab)
    {
        CommandTableModel<?> model = table.getModel();
        table.stopEditing();

        // If the selected row has no option, then use the default option on that row only
        int r = table.getSelectedRow();

        if (r >= 0) {
            int rowIndex = table.convertRowIndexToModel(r);
            if (Util.empty(model.getCode(rowIndex))) {
                R row = table.getModel().getRow(rowIndex);
                Option option = command.getOptions().getDefaultRowOption();
                option.runOption(command, row, newTab);
                return;
            }
        }

        // Apply the options on all rows.
        for (int i = 0; i < model.getRowCount(); i++) {
            String code = model.getCode(i);
            if (!Util.empty(code)) {
                Option option = command.getOptions().getOption(code);
                if (option != null) {
                    if (option.isMultiRow()) {
                        processMultiRowOptions(option, newTab);
                    } else {
                        model.setCode(i, "");
                        option.runOption(command, model.getRow(i), newTab);
                        if (!newTab) {
                            // TODO Should the remaining options be ignore? (if results were replaced).
                            // For now, lets be safe, and only apply a single option.
                            break;
                            // Note, this is bad, because we are NOT doing this in the order as seen in the GUI
                            // we are doing based on the UNSORTED rows.
                        }
                    }
                }
            }
        }
    }

    private void processMultiRowOptions( Option option, boolean newTab )
    {
        String code = option.getCode();
        CommandTableModel<?> model = table.getModel();

        List<Object> rows = new ArrayList<Object>();
        for (int i = 0; i < model.getRowCount(); i++) {
            if ( code.equals(model.getCode(i))) {
                
                model.setCode(i, "");
                rows.add( model.getRow( i ) );
            }
        }
        option.runMultiOption(command, rows, newTab);
    }

    public void createNonRowOptionsMenu(MouseEvent me)
    {        
        boolean useNewTab = me.isControlDown();

        JPopupMenu menu = new JPopupMenu();
        Options options = command.getOptions();
        for (Option option : options) {
            if (!option.isRow()) {
                menu.add(createOptionsMenuItem( option, -1, useNewTab));
            }
        }
        
        menu.show(me.getComponent(), me.getX(), me.getY());
    }

    private void createOptionsMenu(MouseEvent me)
    {
        int r = table.rowAtPoint(me.getPoint());
        if ( r < 0 ) {
            createNonRowOptionsMenu(me);
            return;
        }
        
        int rowIndex = table.convertRowIndexToModel(r);
        table.getSelectionModel().clearSelection();
        table.getSelectionModel().addSelectionInterval(r, r);

        boolean useNewTab = me.isControlDown();

        JPopupMenu menu = new JPopupMenu();
        Options options = command.getOptions();
        for (Option option : options) {
            if (option.isRow()) {
                menu.add(createOptionsMenuItem(option, rowIndex, useNewTab));
            }
        }
        
        boolean first = true;
        for (Option option : options) {
            if (!option.isRow()) {
                if (first) {
                    menu.addSeparator();
                    first = false;
                }
                menu.add(createOptionsMenuItem(option, rowIndex, useNewTab));
            }
        }  
        
        menu.show(me.getComponent(), me.getX(), me.getY());
    }

    private JMenuItem createOptionsMenuItem(final Option option, final int rowIndex, final boolean useNewTab)
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

    public void go()
    {
        command.go();
    }

}
