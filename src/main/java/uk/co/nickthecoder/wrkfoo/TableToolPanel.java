package uk.co.nickthecoder.wrkfoo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.Option;
import uk.co.nickthecoder.wrkfoo.option.Options;

public class TableToolPanel<R> extends ToolPanel
{
    private static final long serialVersionUID = 1L;

    protected SimpleTable<R> table;

    protected TableTool<R> tableTool;

    public TableToolPanel(TableTool<R> tool)
    {
        super(tool);
        tableTool = tool;
    }

    @Override
    public void postCreate()
    {
        super.postCreate();

        @SuppressWarnings("unchecked")
        TableResultsPanel<R> results = (TableResultsPanel<R>) resultsPanel;
        table = results.table;

        MainWindow.putAction("ENTER", "defaultRowAction", table, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
            new AbstractAction()
            {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    processOptions(false);
                }
            });

        MainWindow.putAction("ctrl ENTER", "defaultRowActionNewTab", table, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
            new AbstractAction()
            {
                private static final long serialVersionUID = 1L;

                @Override
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
                    Option option = tableTool.getOptions().getDefaultRowOption(row);
                    getMainWindow().runOption(option, tableTool, row, newTab);
                }

            }
        });

    }

    private void createOptionsMenu(MouseEvent me)
    {
        int r = table.rowAtPoint(me.getPoint());
        if (r < 0) {
            createNonRowOptionsMenu(me);
            return;
        }

        if (table.getSelectedRowCount() > 1) {
            createMultiOptionsMenu(me);
            return;
        }

        int rowIndex = table.convertRowIndexToModel(r);
        table.getSelectionModel().clearSelection();
        table.getSelectionModel().addSelectionInterval(r, r);
        Object row = table.getModel().getRow(rowIndex);

        boolean useNewTab = me.isControlDown();

        JPopupMenu menu = createPopupMenu();

        // Add row options first
        Options options = tableTool.getOptions();
        for (Option option : options) {
            if (option.isRow()) {
                if (option.isApplicable(row)) {
                    menu.add(createMenuItem(option, rowIndex, useNewTab));
                }
            }
        }

        // Add non-row options next
        boolean first = true;
        for (Option option : options) {
            if (!option.isRow()) {
                if (first) {
                    menu.addSeparator();
                    JMenuItem instruction = new JMenuItem("Non-Row Options");
                    instruction.setEnabled(false);
                    menu.add(instruction);
                    first = false;
                }
                menu.add(createMenuItem(option, rowIndex, useNewTab));
            }
        }

        menu.show(me.getComponent(), me.getX(), me.getY());
    }

    private void createMultiOptionsMenu(MouseEvent me)
    {
        JPopupMenu menu = createPopupMenu();

        Options options = tableTool.getOptions();
        for (Option option : options) {
            if (option.isMultiRow()) {
                menu.add(createMultiMenuItem(option));
            }
        }

        menu.show(me.getComponent(), me.getX(), me.getY());
    }

    private JMenuItem createMenuItem(Option option)
    {
        String extra = Util.empty(option.getCode()) ? "" : " (" + option.getCode() + ")";
        String text = option.getLabel() + extra;
        return new JMenuItem(text);
    }

    protected JMenuItem createMenuItem(final Option option, final int rowIndex, final boolean useNewTab)
    {
        JMenuItem item = createMenuItem(option);
        item.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Object row = rowIndex >= 0 ? table.getModel().getRow(rowIndex) : null;
                if (option != null) {
                    getMainWindow().runOption(option, tableTool, row, useNewTab);
                }
            }
        });

        return item;
    }

    private JMenuItem createMultiMenuItem(final Option option)
    {
        JMenuItem item = createMenuItem(option);
        item.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ToolTableModel<?> model = table.getModel();
                List<Object> rows = new ArrayList<>();

                for (int r : table.getSelectedRows()) {
                    // TODO check if I need to convert from view to model
                    Object row = model.getRow(r);

                    if (option.isApplicable(row)) {
                        rows.add(row);
                        table.removeRowSelectionInterval(r, r);
                    }
                }
                getMainWindow().runMultipleOption(option, tableTool, rows, false);
            }
        });

        return item;
    }

    private void processOptions(boolean newTab)
    {
        ToolTableModel<?> model = table.getModel();
        table.stopEditing();

        // Apply the options on all rows.
        boolean foundOne = false;

        for (int i = 0; i < model.getRowCount(); i++) {
            String code = model.getCode(i);
            if (!Util.empty(code)) {
                foundOne = true;

                Object row = model.getRow(i);
                Option option = tableTool.getOptions().getOption(code, row);
                if (option != null) {
                    if (option.isMultiRow()) {
                        processMultiRowOptions(tableTool, option, newTab);
                    } else {
                        model.setCode(i, "");
                        if (!getMainWindow().runOption(option, tableTool, row, newTab)) {
                            model.setCode(i, code); // Put back the code
                            // TODO Should I stop on error?
                            break;
                        }

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

        // Run the default option on the current row if no options have been entered.
        if (!foundOne) {
            int r = table.getSelectedRow();

            if (r >= 0) {
                int rowIndex = table.convertRowIndexToModel(r);
                if (Util.empty(model.getCode(rowIndex))) {
                    R row = table.getModel().getRow(rowIndex);
                    Option option = tableTool.getOptions().getDefaultRowOption(row);
                    getMainWindow().runOption(option, tableTool, row, newTab);

                    return;
                }
            }
        }
    }

    private void processMultiRowOptions(TableTool<?> tableTool, Option option, boolean newTab)
    {
        ToolTableModel<?> model = table.getModel();

        List<Object> rows = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            Object row = model.getRow(i);
            Option otherOption = tableTool.getOptions().getRowOption(model.getCode(i), row);
            if (otherOption == option) {
                model.setCode(i, "");
                rows.add(model.getRow(i));
            }
        }
        getMainWindow().runMultipleOption(option, tableTool, rows, newTab);
    }

    public void stopEditing()
    {
        if (table.isEditing()) {
            if (table.isEditing() && !table.getCellEditor().stopCellEditing()) {
                table.getCellEditor().cancelCellEditing();
            }
        }
    }

}
