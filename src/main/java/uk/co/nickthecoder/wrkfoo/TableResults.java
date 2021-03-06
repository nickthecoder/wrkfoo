package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import uk.co.nickthecoder.wrkfoo.option.Option;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class TableResults<R> extends PanelResults
{
    protected SimpleTable<R> table;

    protected JScrollPane tableScrollPane;

    protected TableTool<?, ?> tableTool;

    protected OptionsRunner optionsRunner;

    public TableResults(TableTool<?, ?> tool, SimpleTable<R> table)
    {
        super(tool);
        this.tableTool = tool;

        this.table = table;
        optionsRunner = new OptionsRunner(tool);

        table.setAutoCreateRowSorter(true);

        tableScrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        getComponent().add(tableScrollPane, BorderLayout.CENTER);

        ActionBuilder builder = new ActionBuilder(this).component(table)
            .condition(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        builder.name("runOptions").buildShortcut();
        builder.name("runOptionsInNewTab").buildShortcut();
        builder.name("promptOptions").buildShortcut();
        builder.name("promptOptionsInNewTab").buildShortcut();

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
                    int rowIndex = table.rowAtPoint(me.getPoint());
                    if (rowIndex >= 0 ) {
                        rowIndex = table.convertRowIndexToModel(rowIndex);
    
                        R row = table.getModel().getRow(rowIndex);
                        Option option = tableTool.getOptions().getRowOption(tableTool, OptionsRunner.DEFAULT_CODE, row);
                        if (option != null) {
                            optionsRunner.runOption(option, row, newTab, false);
                        }
                    }
                }

            }
        });

    }

    @Override
    public JComponent getFocusComponent()
    {
        Focuser.log("TableResultsPanel row count = " + table.getModel().getRowCount());
        return table.getModel().getRowCount() == 0 ? super.getFocusComponent() : table;
    }

    public SimpleTable<R> getTable()
    {
        return table;
    }

    public void onRunOptions()
    {
        optionsRunner.processTableOptions(false, false);
    }

    public void onRunOptionsInNewTab()
    {
        optionsRunner.processTableOptions(true, false);
    }

    public void onPromptOptions()
    {
        optionsRunner.processTableOptions(false, true);
    }

    public void onPromptOptionsInNewTab()
    {
        optionsRunner.processTableOptions(true, true);
    }

}
