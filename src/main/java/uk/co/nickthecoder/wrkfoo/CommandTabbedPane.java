package uk.co.nickthecoder.wrkfoo;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

public class CommandTabbedPane extends JTabbedPane implements Iterable<CommandTab>
{
    private List<CommandTab> commandTabs;

    public CommandTabbedPane()
    {
        commandTabs = new ArrayList<CommandTab>();
        enableReordering();
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

    public void insert(CommandTab tab, int index)
    {
        commandTabs.add(index, tab);

        tab.tabbedPane = this;
        JLabel label = new JLabel(tab.getCommand().getShortTitle());
        label.setIcon(tab.getCommand().getIcon());
        label.setHorizontalTextPosition(JLabel.TRAILING); // Icon on the left

        insertTab("", null, tab.getPanel(), null, index);
        setTabComponentAt(index, label);
    }

    @Override
    public void removeTabAt(int index)
    {
        super.removeTabAt(index);
        commandTabs.get(index).tabbedPane = null;
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

    public CommandTab getCommandTab(int index)
    {
        return this.commandTabs.get(index);
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

    public void enableReordering()
    {
        TabReorderHandler handler = new TabReorderHandler();
        addMouseListener(handler);
        addMouseMotionListener(handler);
    }

    private class TabReorderHandler extends MouseInputAdapter
    {
        private int draggedTabIndex;

        protected TabReorderHandler()
        {
            draggedTabIndex = -1;
        }

        public void mousePressed(MouseEvent e)
        {
            draggedTabIndex = getUI().tabForCoordinate(CommandTabbedPane.this, e.getX(), e.getY());
        }

        public void mouseReleased(MouseEvent e)
        {
            if (draggedTabIndex < 0) {
                return;
            }

            CommandTab tab = getCommandTab(draggedTabIndex);

            if (tab == null) {
                draggedTabIndex = -1;
                return;
            }
            Command<?> command = tab.getCommand();

            MainWindow destinationWindow = MainWindow.getMouseMainWindow();
            if (destinationWindow == null) {

                // Tear off the tab into a new MainWindow
                if (commandTabs.size() > 1) {
                    removeTabAt(draggedTabIndex);

                    MainWindow newWindow = new MainWindow(command);
                    command.go();
                    newWindow.setVisible(true);
                }

            } else if (destinationWindow != tab.getMainWindow()) {
                // Move the tab to a different MainWindow
                MainWindow currentMainWindow = tab.getMainWindow();
                removeTabAt(draggedTabIndex);
                destinationWindow.addTab(command);

                // Current window has no more tabs, so close it.
                if (commandTabs.size() == 0) {
                    currentMainWindow.setVisible(false);
                }
            }
            draggedTabIndex = -1;

        }

        public void mouseDragged(MouseEvent e)
        {
            if (draggedTabIndex == -1) {
                return;
            }

            int targetTabIndex = getUI().tabForCoordinate(CommandTabbedPane.this, e.getX(), e.getY());

            if (targetTabIndex != -1 && targetTabIndex != draggedTabIndex) {

                boolean isForwardDrag = targetTabIndex > draggedTabIndex;

                CommandTab tab = getCommandTab(draggedTabIndex);
                removeTabAt(draggedTabIndex);
                insert(tab, draggedTabIndex + (isForwardDrag ? 1 : -1));

                draggedTabIndex = targetTabIndex;
                setSelectedIndex(draggedTabIndex);
            }
        }
    }

    @Override
    public Iterator<CommandTab> iterator()
    {
        return commandTabs.iterator();
    }

    public void nextTab()
    {
        int newIndex = getSelectedIndex() + 1;
        if (newIndex <= 0) {
            return;
        }
        if (newIndex >= getTabCount()) {
            newIndex = 0;
        }
        setSelectedIndex(newIndex);
    }

    public void previousTab()
    {
        int newIndex = getSelectedIndex() - 1;
        if (newIndex < -1) {
            return;
        }
        if (newIndex == -1) {
            newIndex = getTabCount() - 1;
        }
        setSelectedIndex(newIndex);
    }
}
