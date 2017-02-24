package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableModel;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.wrkfoo.util.ToggleSplitPane;

public class CommandPanel extends JPanel
{
    private Command<?> wrkFoo;

    private ToggleSplitPane splitPane;

    private JPanel sidePanel;

    private ParametersPanel parametersPanel;

    private JPanel body;

    private JButton goButton;

    private JTable table;

    private JScrollPane scrollPane;

    public CommandPanel(Command<?> foo)
    {
        this.wrkFoo = foo;

        sidePanel = new JPanel();
        body = new JPanel();
        body.setLayout(new BorderLayout());

        sidePanel.setLayout(new BorderLayout());
        parametersPanel = foo.createParametersPanel();
        sidePanel.add(parametersPanel, BorderLayout.CENTER);

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

        table = wrkFoo.getResults().createTable();
        table.setAutoCreateRowSorter(true);

        scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        body.add(scrollPane, BorderLayout.CENTER);

        splitPane = new ToggleSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, body, sidePanel, false);
        
        this.setLayout(new BorderLayout());
        this.add(splitPane,BorderLayout.CENTER);

        this.setBackground(Color.blue);
    }

    public void postCreate()
    {

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("F9"), "toggleSidebar");
        actionMap.put("toggleSidebar", new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                splitPane.toggle();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F5"), "refresh");
        actionMap.put("refresh", new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                go();
            }
        });

        goButton.getRootPane().setDefaultButton(goButton);
    }

    public void go()
    {
        System.out.println("CommandPanel.go");
        wrkFoo.run();
        System.out.println("Completed run");

        Results<?> results = wrkFoo.getResults();

        TableModel tableModel = results.getTableModel();
        Columns<?> columns = results.getColumns();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < columns.getColumnCount(); j++) {
                System.out.print(tableModel.getValueAt(i, j));
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println("Mainwindow.go ended");

        table.setModel(wrkFoo.getResults().getTableModel());
    }

}
