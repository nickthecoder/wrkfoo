package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import uk.co.nickthecoder.jguifier.util.AutoExit;

public class MainWindow extends JFrame
{
    private JTabbedPane tabbedPane;

    public MainWindow(Command<?>... commands)
    {
        tabbedPane = new JTabbedPane();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        setTitle("WrkFoo");

        boolean first = true;
        for (Command<?> command : commands) {
            
            if (first) {
                this.setTitle(command.getTitle());
            }
            
            addTab( command );
            command.go();
        }

        setLocationRelativeTo(null);
        pack();
    }
    
    private void addTab( Command<?> command )
    {
        CommandTab tab = new CommandTab( command );
        
        CommandPanel<?> commandPanel = command.getCommandPanel();

        JLabel label = new JLabel(command.getTitle());
        label.setIcon(command.getIcon());
        // label.setIconTextGap(5);
        label.setHorizontalTextPosition(JLabel.TRAILING); // Icon on the left

        tabbedPane.addTab(null, tab.getPanel());
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount()-1, label);
        
        commandPanel.postCreate();
        tab.postCreate();
    }

    public void setVisible(boolean show)
    {
        super.setVisible(show);
        AutoExit.setVisible(show);
    }
    

    public void putAction(String keyStroke, String name, Action action)
    {
        putAction(keyStroke, name, this.getRootPane(), action);
    }

    public static void putAction(String keyStroke, String name, JComponent component, Action action)
    {
        putAction(keyStroke, name, component, JComponent.WHEN_IN_FOCUSED_WINDOW, action);
    }

    public static void putAction(String key, String name, JComponent component, int condition, Action action)
    {
        InputMap inputMap = component.getInputMap(condition);
        ActionMap actionMap = component.getActionMap();

        KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
        inputMap.put(keyStroke, name);
        actionMap.put(name, action);

        // Bodge! I don't want the table stealing any of MY keyboard shortcuts
        // TODO table.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, name);
    }
}
