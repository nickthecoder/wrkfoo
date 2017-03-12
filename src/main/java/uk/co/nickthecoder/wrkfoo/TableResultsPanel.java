package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

public class TableResultsPanel<R> extends ResultsPanel
{
    private static final long serialVersionUID = 1L;

    public SimpleTable<R> table;

    public JScrollPane tableScrollPane;

    public TableResultsPanel(SimpleTable<R> table)
    {
        super();
        this.table = table;
        table.setAutoCreateRowSorter(true);

        tableScrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        this.add(tableScrollPane, BorderLayout.CENTER);
    }

}
