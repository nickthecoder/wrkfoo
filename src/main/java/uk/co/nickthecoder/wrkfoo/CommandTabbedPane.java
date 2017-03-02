package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class CommandTabbedPane extends JTabbedPane
{
    private List<CommandTab> commandTabs;

    public CommandTabbedPane()
    {
        commandTabs = new ArrayList<CommandTab>();
    }

    public CommandTab getCurrentTab()
    {
        if (commandTabs.size() == 0) {
            return null;
        }
        return commandTabs.get(getSelectedIndex());
    }

    public void add(CommandTab tab)
    {
        commandTabs.add(tab);
        tab.tabbedPane = this;
        JLabel label = new JLabel(tab.getCommand().getShortTitle());
        label.setIcon(tab.getCommand().getIcon());
        label.setHorizontalTextPosition(JLabel.TRAILING); // Icon on the left

        addTab(null, tab.getPanel());
        setTabComponentAt(getTabCount() - 1, label);
    }

    @Override
    public void removeTabAt(int index)
    {
        super.removeTabAt(index);
        commandTabs.remove(index);
    }

    public void updateTabInfo(CommandTab tab)
    {
        String title = tab.getCommand().getShortTitle();
        Icon icon = tab.getCommand().getIcon();
        String longTitle = tab.getCommand().getLongTitle();

        int index = commandTabs.indexOf(tab);
        JLabel label = (JLabel) getTabComponentAt(index);
        label.setText(title);
        label.setIcon(icon);
        if (getSelectedIndex() == index) {
            ((JFrame) SwingUtilities.getRoot(this)).setTitle(longTitle);
        }
    }

    public CommandTab getSelectedCommandTab()
    {
        int index = getSelectedIndex();
        if (index >= 0) {
            return commandTabs.get(index);
        } else {
            return null;
        }
    }
}
