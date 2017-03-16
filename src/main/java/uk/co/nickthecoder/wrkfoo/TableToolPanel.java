package uk.co.nickthecoder.wrkfoo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import uk.co.nickthecoder.wrkfoo.option.Option;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

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

        ActionBuilder builder = new ActionBuilder(this).component(table)
            .condition(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        builder.name("runOptions").shortcut("ENTER").buildShortcut();
        builder.name("runOptionsInNewTab").shortcut("ctrl ENTER").buildShortcut();

        table.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent me)
            {
                if (me.isPopupTrigger()) {
                    optionsRunner.popupRowMenu(me);
                }
            }

            @Override
            public void mousePressed(MouseEvent me)
            {
                if (me.isPopupTrigger()) {
                    optionsRunner.popupRowMenu(me);
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

    public void onRunOptions()
    {
        optionsRunner.processTableOptions(false);
    }

    public void onRunOptionsInNewTab()
    {
        optionsRunner.processTableOptions(true);
    }

    public SimpleTable<R> getTable()
    {
        return table;
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
