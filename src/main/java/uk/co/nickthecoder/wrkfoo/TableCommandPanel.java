package uk.co.nickthecoder.wrkfoo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.Option;
import uk.co.nickthecoder.wrkfoo.option.Options;

public class TableCommandPanel<R> extends CommandPanel
{
    protected SimpleTable<R> table;

    protected TableCommand<R> tableCommand;
    
    public TableCommandPanel(TableCommand<R> command)
    {
        super(command);
        tableCommand = command;
    }

    public void postCreate()
    {
        super.postCreate();

        // TODO Is there a better design pattern than this? It works, but looks ugly.
        @SuppressWarnings("unchecked")
        TableResults<R> results = (TableResults<R>) resultsComponent;
        table = results.table;
        
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
                    Option option = tableCommand.getOptions().getDefaultRowOption(row);
                    option.runOption(tableCommand, row, newTab);
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

        int rowIndex = table.convertRowIndexToModel(r);
        table.getSelectionModel().clearSelection();
        table.getSelectionModel().addSelectionInterval(r, r);
        Object row = table.getModel().getRow(rowIndex);

        boolean useNewTab = me.isControlDown();

        JPopupMenu menu = new JPopupMenu();
        Options options = tableCommand.getOptions();
        for (Option option : options) {
            if (option.isRow()) {
                if (option.isApplicable(row)) {
                    menu.add(createOptionsMenuItem(option, rowIndex, useNewTab));
                }
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
                Option option = tableCommand.getOptions().getDefaultRowOption(row);
                option.runOption(tableCommand, row, newTab);
                return;
            }
        }

        // Apply the options on all rows.
        for (int i = 0; i < model.getRowCount(); i++) {
            String code = model.getCode(i);
            if (!Util.empty(code)) {
                Option option = tableCommand.getOptions().getOption(code);
                if (option != null) {
                    if (option.isMultiRow()) {
                        processMultiRowOptions(option, newTab);
                    } else {
                        model.setCode(i, "");
                        option.runOption(tableCommand, model.getRow(i), newTab);
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
                    option.runOption(tableCommand, row, useNewTab);
                }
            }
        });

        return item;
    }
    
    private void processMultiRowOptions(Option option, boolean newTab)
    {
        String code = option.getCode();
        CommandTableModel<?> model = table.getModel();

        List<Object> rows = new ArrayList<Object>();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (code.equals(model.getCode(i))) {

                model.setCode(i, "");
                rows.add(model.getRow(i));
            }
        }
        option.runMultiOption(tableCommand, rows, newTab);
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
