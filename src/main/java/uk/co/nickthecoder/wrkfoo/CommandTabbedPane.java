package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import uk.co.nickthecoder.wrkfoo.command.NullCommand;

public class CommandTabbedPane extends JTabbedPane
{
    private static final CommandTab DUMMY_COMMAND_TAB = new CommandTab(new NullCommand());

    private List<CommandTab> commandTabs;

    public CommandTabbedPane()
    {
        commandTabs = new ArrayList<CommandTab>();
    }

    public CommandTab getCurrentTab()
    {
        if (commandTabs.size() == 0) {
            return DUMMY_COMMAND_TAB;
        }
        return commandTabs.get(getSelectedIndex());
    }

    public void add(CommandTab tab)
    {
        commandTabs.add( tab );
        tab.tabbedPane = this;
        JLabel label = new JLabel(tab.getCommand().getTitle());
        label.setIcon(tab.getCommand().getIcon());
        label.setHorizontalTextPosition(JLabel.TRAILING); // Icon on the left

        addTab(null, tab.getPanel());
        setTabComponentAt(getTabCount() - 1, label);

    }

    public void setTabInfo(CommandTab tab, String title, Icon icon)
    {
        JLabel label = (JLabel) getTabComponentAt(commandTabs.indexOf(tab));
        label.setText(title);
        label.setIcon(icon);
    }

    public CommandTab getSelectedCommandTab()
    {
        if (commandTabs.size() == 0) {
            return null;
        }
        return commandTabs.get(getSelectedIndex());
    }
}
