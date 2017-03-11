package uk.co.nickthecoder.wrkfoo;

import java.awt.KeyboardFocusManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import uk.co.nickthecoder.jguifier.StringParameter;
import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class TabbedPane extends JTabbedPane implements Iterable<ToolTab>
{
    private List<ToolTab> toolTabs;

    public TabbedPane()
    {
        toolTabs = new ArrayList<ToolTab>();
        enableReordering();
    }

    public ToolTab getCurrentTab()
    {
        if (toolTabs.size() == 0) {
            return null;
        }
        int index = getSelectedIndex();
        if (index < 0) {
            return null;
        }
        if (index >= toolTabs.size()) {
            return null;
        }
        return toolTabs.get(index);
    }

    public void insert(final ToolTab tab)
    {
        int index = getSelectedIndex() + 1;
        add(tab, index);
    }

    public void add(final ToolTab tab)
    {
        add(tab, toolTabs.size());
    }

    public void add(final ToolTab tab, int position)
    {
        JLabel tabLabel = new JLabel(tab.getTitle());
        tabLabel.setIcon(tab.getTool().getIcon());
        tabLabel.setHorizontalTextPosition(JLabel.TRAILING); // Icon on the left

        JPanel panel = tab.getPanel();
        panel.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
            }
        });

        insertTab(null, null, panel, null, position);
        toolTabs.add(position, tab);
        setTabComponentAt(position, tabLabel);
    
        tab.setTabbedPane(this);
    }


    private JPopupMenu createTabPopupMenu(MouseEvent me)
    {
        JPopupMenu menu = new JPopupMenu();

        ActionBuilder builder = new ActionBuilder(this).exceptionHandler(getMainWindow());
        menu.add(builder.label("Rename Tab").shortcut("ctrl R").action("onRenameTab").buildMenuItem());
        menu.add(builder.label("Close Tab").shortcut("ctrl W").action("onCloseTab").buildMenuItem());

        menu.show(me.getComponent(), me.getX(), me.getY());

        return menu;
    }

    public MainWindow getMainWindow()
    {
        return MainWindow.getMainWindow(this);
    }

    public void onRenameTab()
    {
        new RenameTabTask(getSelectedIndex()).neverExit().promptTask();
    }

    public void onCloseTab()
    {
        removeTabAt(getSelectedIndex());
    }

    public class RenameTabTask extends Task
    {
        public StringParameter name = new StringParameter.Builder("name").parameter();
        private int tabIndex;

        public RenameTabTask(int tabIndex)
        {
            super();
            this.tabIndex = tabIndex;
            name.setDefaultValue(getToolTab(tabIndex).getTitleTemplate());
            addParameters(name);
        }

        @Override
        public void body()
        {
            ToolTab tab = getToolTab(tabIndex);
            tab.setTitleTemplate(name.getValue());
            MainWindow.getMainWindow(TabbedPane.this).changedTab();
            ((JLabel) getTabComponentAt(tabIndex)).setText(tab.getTitle());
        }
    }

    public void insert(ToolTab tab, int index)
    {
        toolTabs.add(index, tab);

        tab.setTabbedPane(this);
        JLabel label = new JLabel(tab.getTool().getToolTab().getTitle());
        label.setIcon(tab.getTool().getIcon());
        label.setHorizontalTextPosition(JLabel.TRAILING); // Icon on the left

        insertTab("", null, tab.getPanel(), null, index);
        setTabComponentAt(index, label);
    }

    @Override
    public void removeTabAt(int index)
    {
        ToolTab tab = toolTabs.get(index);
        
        toolTabs.remove(index);
        super.removeTabAt(index);

        if (getSelectedIndex() == index) {
            if (index > 0) {
                setSelectedIndex(index - 1);
            }
        }

        tab.setTabbedPane(null);

    }

    public void removeAllTabs()
    {
        for (int i = getTabCount() - 1; i >= 0; i--) {
            removeTabAt(i);
        }
    }

    public void updateTabInfo(ToolTab tab)
    {
        String title = tab.getTitle();
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

    @Override
    public void setSelectedIndex(int i)
    {
        super.setSelectedIndex(i);
        getComponentAt(i).requestFocus();
    }

    public void setSelectedToolTab(ToolTab tab)
    {
        for (int i = 0; i < getTabCount(); i++) {
            if (this.toolTabs.get(i) == tab) {
                setSelectedIndex(i);
            }
        }
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
            if (e.isPopupTrigger()) {
                createTabPopupMenu(e);
                return;
            }
            draggedTabIndex = getUI().tabForCoordinate(TabbedPane.this, e.getX(), e.getY());
        }

        public void mouseReleased(MouseEvent e)
        {
            if (e.isPopupTrigger()) {
                createTabPopupMenu(e);
                return;
            }
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

            } else if (destinationWindow != MainWindow.getMainWindow(tab.getPanel())) {
                // Move the tab to a different MainWindow
                MainWindow currentMainWindow = MainWindow.getMainWindow(tab.getPanel());
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

            int targetTabIndex = getUI().tabForCoordinate(TabbedPane.this, e.getX(), e.getY());

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
