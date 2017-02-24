package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import uk.co.nickthecoder.jguifier.ParametersPanel;
import uk.co.nickthecoder.wrkfoo.util.ToggleSplitPane;

public class CommandPanel<R> extends JPanel
{
    private Command<R> command;

    private ToggleSplitPane splitPane;

    private JPanel sidePanel;

    private ParametersPanel parametersPanel;

    private JPanel body;

    private JButton goButton;

    public JTable table;

    private JScrollPane scrollPane;

    public CommandPanel(Command<R> foo)
    {
        this.command = foo;

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

        table = command.getResults().createTable();
        table.setAutoCreateRowSorter(true);

        scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        body.add(scrollPane, BorderLayout.CENTER);

        splitPane = new ToggleSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, body, sidePanel, false);

        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);

        this.setBackground(Color.blue);
    }

    public void putAction(String keyStroke, String name, Action action)
    {
        putAction(keyStroke, name, this, action);
    }

    public void putAction(String keyStroke, String name, JComponent component, Action action)
    {
        putAction(keyStroke, name, component, JComponent.WHEN_IN_FOCUSED_WINDOW, action );
    }
    
    public void putAction(String key, String name, JComponent component, int condition, Action action)
    {
        InputMap inputMap = component.getInputMap(condition);
        ActionMap actionMap = component.getActionMap();

        KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
        inputMap.put( keyStroke, name);
        actionMap.put(name, action);
        
        // Bodge! I don't want the table stealing any of MY keyboard shortcuts
        table.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, name);
    }

    public void postCreate()
    {
        command.postCreate(this);
        
        putAction("F9", "toggleSidebar", new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                splitPane.toggle();
            }
        });

        putAction("F5", "refresh", new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                go();
            }
        });

        putAction("ENTER", "defaultRowAction", table, JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                int r = table.convertRowIndexToModel(table.getSelectedRow());
                R row = command.getResults().getRow(r);
                command.defaultAction(row);
            }
        });
        
        table.addMouseListener(new MouseAdapter()
        {

            public void mouseClicked(MouseEvent me)
            {
                if (me.getClickCount() == 2) {
                    int r = table.convertRowIndexToModel(table.getSelectedRow());
                    R row = command.getResults().getRow(r);
                    command.defaultAction(row);
                }
            }
        });

        goButton.getRootPane().setDefaultButton(goButton);
    }

    public void go()
    {
        command.go();
    }

}
