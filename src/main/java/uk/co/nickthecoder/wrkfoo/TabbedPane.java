package uk.co.nickthecoder.wrkfoo;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.MouseInputAdapter;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class TabbedPane extends JTabbedPane implements Iterable<ToolTab>
{
    private static final long serialVersionUID = 1L;

    private List<ToolTab> toolTabs;

    public TabbedPane()
    {
        toolTabs = new ArrayList<>();
        enableReordering();

        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setTabPlacement(JTabbedPane.LEFT);
    }

    public ToolTab getSelectedTab()
    {
        WrkFoo.assertIsEDT();

        if (toolTabs.size() == 0) {
            return null;
        }
        int index = getSelectedIndex();
        if (index < 0) {
            return null;
        }

        return toolTabs.get(index);
    }

    public void insert(final ToolTab tab)
    {
        WrkFoo.assertIsEDT();

        int index = getSelectedIndex() + 1;
        add(tab, index);
    }

    public void add(final ToolTab tab)
    {
        WrkFoo.assertIsEDT();

        add(tab, toolTabs.size());
    }

    public void add(final ToolTab tab, int position)
    {
        WrkFoo.assertIsEDT();

        JLabel tabLabel = new JLabel(tab.getTitle());
        tabLabel.setIcon(tab.getTool().getIcon());
        // tabLabel.setHorizontalTextPosition(SwingConstants.TRAILING); // Icon on the left
        tabLabel.setHorizontalAlignment(LEFT);
        tabLabel.setPreferredSize(new Dimension(150, tabLabel.getPreferredSize().height));

        JPanel panel = tab.getPanel();

        insertTab(null, null, panel, null, position);
        toolTabs.add(position, tab);
        setTabComponentAt(position, tabLabel);

        tab.setTabbedPane(this);

        TabNotifier.fireAttached(tab);
    }

    public void removeTab(ToolTab tab)
    {
        int index = toolTabs.indexOf(tab);
        if (index >= 0) {
            removeTabAt(index);
        }
    }

    @Override
    public void removeTabAt(int index)
    {
        WrkFoo.assertIsEDT();

        ToolTab tab = toolTabs.get(index);
        TabNotifier.fireDetaching(tab);

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
        WrkFoo.assertIsEDT();

        for (int i = getTabCount() - 1; i >= 0; i--) {
            removeTabAt(i);
        }
    }

    private JPopupMenu createTabPopupMenu(MouseEvent me)
    {
        WrkFoo.assertIsEDT();

        JPopupMenu menu = new JPopupMenu();

        ActionBuilder builder = new ActionBuilder(this);
        menu.add(builder.name("tab.properties").label("Tab Promperties").buildMenuItem());
        menu.add(builder.name("tab.close").label("Close Tab").buildMenuItem());

        menu.show(me.getComponent(), me.getX(), me.getY());

        return menu;
    }

    public void onProperties()
    {
        new TabPropertiesTask(getSelectedIndex()).promptTask();
    }

    public void onClose()
    {
        removeTabAt(getSelectedIndex());
    }

    private class TabPropertiesTask extends Task
    {
        private StringParameter title = new StringParameter.Builder("title")
            .description("Tab Label (%t is replaced by the normal title)")
            .parameter();

        private StringParameter shortcut = new StringParameter.Builder("shortcut")
            .description("Format : [ctrl|shift|alt]* KEY_NAME")
            .optional().parameter();

        private int tabIndex;

        public TabPropertiesTask(int tabIndex)
        {
            super();
            this.tabIndex = tabIndex;
            ToolTab tab = getToolTab(tabIndex);

            title.setDefaultValue(tab.getTitleTemplate());
            shortcut.setDefaultValue(tab.getShortcut());

            addParameters(title, shortcut);
        }

        @Override
        public void body()
        {
            ToolTab tab = getToolTab(tabIndex);

            tab.setTitleTemplate(title.getValue());
            tab.setShortcut(shortcut.getValue());

            TabNotifier.fireChangedTitle(tab);
        }
    }

    void updateTitle(ToolTab tab)
    {
        WrkFoo.assertIsEDT();

        int index = toolTabs.indexOf(tab);
        if (index >= 0) {
            String title = tab.getTitle();
            Icon icon = tab.getTool().getIcon();

            JLabel label = (JLabel) getTabComponentAt(index);
            label.setText(title);
            label.setIcon(icon);
        }
    }

    public ToolTab getToolTab(int index)
    {
        WrkFoo.assertIsEDT();

        return this.toolTabs.get(index);
    }

    @Override
    public void setSelectedIndex(int i)
    {
        WrkFoo.assertIsEDT();

        ToolTab selectedTab = this.getSelectedTab();
        if (selectedTab != null) {
            TabNotifier.fireDeselecting(selectedTab);
        }

        super.setSelectedIndex(i);
        if ((i >= 0) && (i < toolTabs.size())) {
            Focuser.focusLater("TabbedPane.selected. Results",
                this.toolTabs.get(i).getTool().getResultsPanel().getFocusComponent(), 4);

            TabNotifier.fireSelected(getSelectedTab());
        }
    }

    public void setSelectedToolTab(ToolTab tab)
    {
        WrkFoo.assertIsEDT();

        for (int i = 0; i < getTabCount(); i++) {
            if (this.toolTabs.get(i) == tab) {
                setSelectedIndex(i);
                return;
            }
        }
    }

    public void enableReordering()
    {
        TabReorderHandler handler = new TabReorderHandler();
        addMouseListener(handler);
        addMouseMotionListener(handler);
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

    private class TabReorderHandler extends MouseInputAdapter
    {
        private int draggedTabIndex;

        protected TabReorderHandler()
        {
            draggedTabIndex = -1;
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            if (e.isPopupTrigger()) {
                createTabPopupMenu(e);
                return;
            }
            draggedTabIndex = getUI().tabForCoordinate(TabbedPane.this, e.getX(), e.getY());
        }

        @Override
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
            Tool<?> tool = tab.getTool();

            TopLevel destinationWindow = MainWindow.getMouseMainWindow();
            if (destinationWindow == null) {
                // Tear off the tab into a new MainWindow

                if (toolTabs.size() > 1) {
                    removeTabAt(draggedTabIndex);

                    MainWindow newWindow = new MainWindow();
                    tool.go();

                    ToolTab newTab = newWindow.addTab(tool);

                    newTab.setTitleTemplate(tab.getTitleTemplate());
                    newTab.setShortcut(tab.getShortcut());

                    newWindow.setVisible(true);
                }

            } else if (destinationWindow != tool.getToolPanel().getTopLevel()) {
                // Move the tab to a different MainWindow

                TopLevel currentMainWindow = tool.getToolPanel().getTopLevel();
                removeTabAt(draggedTabIndex);
                ToolTab newTab = destinationWindow.addTab(tool);

                newTab.setTitleTemplate(tab.getTitleTemplate());
                newTab.setShortcut(tab.getShortcut());

                // Current window has no more tabs, so close it.
                if (toolTabs.size() == 0) {
                    currentMainWindow.setVisible(false);
                }
            }
            // else Drags to the same window need no extra code here, the tabs are moved dynamically
            // as the mouse moves. So nothing to do on release.

            draggedTabIndex = -1;

        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            if (draggedTabIndex == -1) {
                return;
            }

            int targetTabIndex = getUI().tabForCoordinate(TabbedPane.this, e.getX(), e.getY());

            if (targetTabIndex != -1 && targetTabIndex != draggedTabIndex) {

                boolean isForwardDrag = targetTabIndex > draggedTabIndex;
                int newIndex = draggedTabIndex + (isForwardDrag ? 1 : -1);
                
                ToolTab tab = getToolTab(draggedTabIndex);
                removeTabAt(draggedTabIndex);
                add(tab, newIndex);

                draggedTabIndex = newIndex;
                setSelectedIndex(draggedTabIndex);
            }
        }
    }

}
