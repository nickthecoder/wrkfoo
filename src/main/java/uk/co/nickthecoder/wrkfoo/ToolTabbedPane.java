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

public class ToolTabbedPane extends JTabbedPane implements Iterable<ToolTab>
{
    private List<ToolTab> toolTabs;

    public ToolTabbedPane()
    {
        toolTabs = new ArrayList<ToolTab>();
        enableReordering();
    }

    public ToolTab getCurrentTab()
    {
        if (toolTabs.size() == 0) {
            return null;
        }
        return toolTabs.get(getSelectedIndex());
    }

    public void add(ToolTab tab)
    {
        toolTabs.add(tab);

        tab.tabbedPane = this;
        JLabel label = new JLabel(tab.getTool().getShortTitle());
        label.setIcon(tab.getTool().getIcon());
        label.setHorizontalTextPosition(JLabel.TRAILING); // Icon on the left

        addTab(null, tab.getPanel());
        setTabComponentAt(getTabCount() - 1, label);
    }

    public void insert(ToolTab tab, int index)
    {
        toolTabs.add(index, tab);

        tab.tabbedPane = this;
        JLabel label = new JLabel(tab.getTool().getShortTitle());
        label.setIcon(tab.getTool().getIcon());
        label.setHorizontalTextPosition(JLabel.TRAILING); // Icon on the left

        insertTab("", null, tab.getPanel(), null, index);
        setTabComponentAt(index, label);
    }

    @Override
    public void removeTabAt(int index)
    {
        super.removeTabAt(index);
        toolTabs.get(index).tabbedPane = null;
        toolTabs.remove(index);
    }

    public void updateTabInfo(ToolTab tab)
    {
        String title = tab.getTool().getShortTitle();
        Icon icon = tab.getTool().getIcon();
        String longTitle = tab.getTool().getLongTitle();

        int index = toolTabs.indexOf(tab);
        JLabel label = (JLabel) getTabComponentAt(index);
        label.setText(title);
        label.setIcon(icon);
        if (getSelectedIndex() == index) {
            ((JFrame) SwingUtilities.getRoot(this)).setTitle(longTitle);
        }
    }

    public ToolTab getToolTab(int index)
    {
        return this.toolTabs.get(index);
    }

    public ToolTab getSelectedToolTab()
    {
        int index = getSelectedIndex();
        if (index >= 0) {
            return toolTabs.get(index);
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
            draggedTabIndex = getUI().tabForCoordinate(ToolTabbedPane.this, e.getX(), e.getY());
        }

        public void mouseReleased(MouseEvent e)
        {
            if (draggedTabIndex < 0) {
                return;
            }

            ToolTab tab = getToolTab(draggedTabIndex);

            if (tab == null) {
                draggedTabIndex = -1;
                return;
            }
            Tool tool = tab.getTool();

            MainWindow destinationWindow = MainWindow.getMouseMainWindow();
            if (destinationWindow == null) {

                // Tear off the tab into a new MainWindow
                if (toolTabs.size() > 1) {
                    removeTabAt(draggedTabIndex);

                    MainWindow newWindow = new MainWindow(tool);
                    tool.go();
                    newWindow.setVisible(true);
                }

            } else if (destinationWindow != tab.getMainWindow()) {
                // Move the tab to a different MainWindow
                MainWindow currentMainWindow = tab.getMainWindow();
                removeTabAt(draggedTabIndex);
                destinationWindow.addTab(tool);

                // Current window has no more tabs, so close it.
                if (toolTabs.size() == 0) {
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

            int targetTabIndex = getUI().tabForCoordinate(ToolTabbedPane.this, e.getX(), e.getY());

            if (targetTabIndex != -1 && targetTabIndex != draggedTabIndex) {

                boolean isForwardDrag = targetTabIndex > draggedTabIndex;

                ToolTab tab = getToolTab(draggedTabIndex);
                removeTabAt(draggedTabIndex);
                insert(tab, draggedTabIndex + (isForwardDrag ? 1 : -1));

                draggedTabIndex = targetTabIndex;
                setSelectedIndex(draggedTabIndex);
            }
        }
    }

    @Override
    public Iterator<ToolTab> iterator()
    {
        return toolTabs.iterator();
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
