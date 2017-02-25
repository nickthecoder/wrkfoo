package uk.co.nickthecoder.wrkfoo;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

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
        int i = 0;
        for (Command<?> command : commands) {
            if (first) {
                this.setTitle(command.getTitle());
            }
            CommandPanel<?> commandPanel = command.createCommandPanel();

            JLabel label = new JLabel(command.getTitle());
            label.setIcon(command.getIcon());
            // label.setIconTextGap(5);
            label.setHorizontalTextPosition(JLabel.TRAILING); // Icon on the left

            tabbedPane.addTab(null, commandPanel);
            tabbedPane.setTabComponentAt(i, label);
            commandPanel.postCreate();

            i++;
        }

        setLocationRelativeTo(null);
        pack();
    }

    public void setVisible(boolean show)
    {
        super.setVisible(show);
        AutoExit.setVisible(show);
    }
}
