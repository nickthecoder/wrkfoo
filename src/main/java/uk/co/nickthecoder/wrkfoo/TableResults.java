package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TableResults<R> extends JPanel
{
    public SimpleTable<R> table;

    public JScrollPane tableScrollPane;

    public TableResults(SimpleTable<R> table)
    {
        this.setLayout(new BorderLayout());
        this.table = table;
        table.setAutoCreateRowSorter(true);

        tableScrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        this.add(tableScrollPane, BorderLayout.CENTER);
    }

}
