package uk.co.nickthecoder.wrkfoo;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.Option;

public class TableToolPanel<R> extends ToolPanel
{
    private static final long serialVersionUID = 1L;

    protected SimpleTable<R> table;

    protected TableTool<R> tableTool;

    protected OptionsRunner optionsRunner;

    public TableToolPanel(TableTool<R> tool)
    {
        super(tool);
        tableTool = tool;
        optionsRunner = new OptionsRunner(tool);
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

        MainWindow.putAction("ctrl ENTER", "defaultRowActionNewTab", table,
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
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
                    optionsRunner.createOptionsMenu(me);
                }
            }

            @Override
            public void mousePressed(MouseEvent me)
            {
                if (me.isPopupTrigger()) {
                    optionsRunner.createOptionsMenu(me);
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
                    optionsRunner.runOption(option, row, newTab);
                }

            }
        });

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
                        if (!optionsRunner.runOption(option, row, newTab)) {
                            model.setCode(i, code); // Put back the code
                            // TODO Should I stop on error?
                            break;
                        }

                        if (!newTab) {
                            // TODO Should the remaining options be ignore? (if message were replaced).
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
                    optionsRunner.runOption(option, row, newTab);

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
        optionsRunner.runMultipleOption(option, rows, newTab);
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
