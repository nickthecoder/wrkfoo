package uk.co.nickthecoder.wrkfoo;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.jguifier.parameter.StringParameter;
import uk.co.nickthecoder.wrkfoo.util.ActionBuilder;

public class MainTabs implements Iterable<Tab>, ChangeListener
{
    private JTabbedPane tabbedPane;

    private List<Tab> tabs;

    /**
     * The currently selected tab's index.
     * -1 for no selected tab.
     */
    private int selectedIndex = -1;    

    public MainTabs()
    {
        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(this);
        tabs = new ArrayList<>();
        enableReordering();

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
    }

    public JComponent getComponent()
    {
        return tabbedPane;
    }

    public int getSelectedIndex()
    {
        return tabbedPane.getSelectedIndex();
    }

    public int getTabCount()
    {
        return tabbedPane.getTabCount();
    }

    public Tab getSelectedTab()
    {
        if (tabs.size() == 0) {
            return null;
        }
        if (selectedIndex < 0) {
            return null;
        }

        return tabs.get(selectedIndex);
    }

    public void insert(final Tab tab)
    {
        WrkFoo.assertIsEDT();

        int index = tabbedPane.getSelectedIndex() + 1;
        add(tab, index);
    }

    public void add(final Tab tab)
    {
        WrkFoo.assertIsEDT();

        add(tab, tabs.size());
    }

    public void add(final Tab tab, int position)
    {
        WrkFoo.assertIsEDT();

        tabs.add(position, tab);

        JLabel tabLabel = new JLabel(tab.getTitle());
        tabLabel.setIcon(tab.getMainHalfTab().getTool().getIcon());
        // tabLabel.setHorizontalTextPosition(SwingConstants.TRAILING); // Icon on the left
        tabLabel.setHorizontalAlignment(JTabbedPane.LEFT);
        tabLabel.setPreferredSize(new Dimension(150, tabLabel.getPreferredSize().height));

        JComponent tabComponent = tab.getComponent();

        tabbedPane.insertTab(null, null, tabComponent, null, position);
        tabbedPane.setTabComponentAt(position, tabLabel);

        tab.setMainTabs(this);

        TabNotifier.fireAttached(tab.getMainHalfTab());
        if (tab.getOtherHalfTab() != null) {
            TabNotifier.fireAttached(tab.getOtherHalfTab());
        }
        TabNotifier.fireAttached(tab);
    }

    public void removeTab(Tab tab)
    {
        int index = tabs.indexOf(tab);
        if (index >= 0) {
            removeTabAt(index);
        }
    }

    public void removeTabAt(int index)
    {
        WrkFoo.assertIsEDT();

        // If we are going to remove the selected tab, then select a different tab first, otherwise the
        // change of selected tab will happen while we are in an inconsistent state.
        if ( index == selectedIndex) {
            int newSelected = index -1;
            if ( tabs.size() <= 1 ) {
                newSelected = -1;
            } else if ( newSelected < 0) {
                newSelected = 0;
            }
            setSelectedIndex(newSelected);
        }

        Tab tab = tabs.get(index);
        
        TabNotifier.fireDetaching(tab.getMainHalfTab());
        if ( tab.getOtherHalfTab() != null) {
            TabNotifier.fireDetaching(tab.getOtherHalfTab());            
        }
        TabNotifier.fireDetaching(tab);

        tabs.remove(index);
        tabbedPane.removeTabAt(index);

        tab.setMainTabs(null);
    }

    public void removeAllTabs()
    {
        WrkFoo.assertIsEDT();

        for (int i = tabbedPane.getTabCount() - 1; i >= 0; i--) {
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
        new TabPropertiesTask(tabbedPane.getSelectedIndex()).promptTask();
    }

    public void onClose()
    {
        removeTabAt(tabbedPane.getSelectedIndex());
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
            Tab tab = getTab(tabIndex);

            title.setDefaultValue(tab.getTitleTemplate());
            shortcut.setDefaultValue(tab.getShortcut());

            addParameters(title, shortcut);
        }

        @Override
        public void body()
        {
            Tab tab = getTab(tabIndex);

            tab.setTitleTemplate(title.getValue());
            tab.setShortcut(shortcut.getValue());

            TabNotifier.fireChangedTitle(tab);
        }
    }

    void updateTitle(Tab tab)
    {
        WrkFoo.assertIsEDT();

        int index = tabs.indexOf(tab);
        if (index >= 0) {
            String title = tab.getTitle();
            Icon icon = tab.getMainHalfTab().getTool().getIcon();

            JLabel label = (JLabel) tabbedPane.getTabComponentAt(index);
            label.setText(title);
            label.setIcon(icon);
        }
    }

    public Tab getTab(int index)
    {
        WrkFoo.assertIsEDT();

        return this.tabs.get(index);
    }

    /**
     * Called when the selected tab has changed.
     */
    @Override
    public void stateChanged(ChangeEvent e)
    {
        if (selectedIndex >= 0) {
            TabNotifier.fireDeselecting(getTab(selectedIndex));
        }

        selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex >= 0) {
            Focuser.focusLater("MainTabs.selected. Results",
                getSelectedTab().getMainHalfTab().getTool().getResultsPanel().getFocusComponent(), 4);

            TabNotifier.fireSelected(getSelectedTab());
        }
    }

    public void setSelectedIndex(int i)
    {
        WrkFoo.assertIsEDT();
        tabbedPane.setSelectedIndex(i);
    }

    public void setSelectedTab(Tab tab)
    {
        WrkFoo.assertIsEDT();

        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (this.tabs.get(i) == tab) {
                setSelectedIndex(i);
                return;
            }
        }
    }

    public void enableReordering()
    {
        TabReorderHandler handler = new TabReorderHandler();
        tabbedPane.addMouseListener(handler);
        tabbedPane.addMouseMotionListener(handler);
    }

    @Override
    public Iterator<Tab> iterator()
    {
        return tabs.iterator();
    }

    public void nextTab()
    {
        int newIndex = tabbedPane.getSelectedIndex() + 1;
        if (newIndex <= 0) {
            return;
        }
        if (newIndex >= tabbedPane.getTabCount()) {
            newIndex = 0;
        }
        setSelectedIndex(newIndex);
    }

    public void previousTab()
    {
        int newIndex = tabbedPane.getSelectedIndex() - 1;
        if (newIndex < -1) {
            return;
        }
        if (newIndex == -1) {
            newIndex = tabbedPane.getTabCount() - 1;
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
            draggedTabIndex = tabbedPane.getUI().tabForCoordinate(tabbedPane, e.getX(), e.getY());
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

            Tab tab = getTab(draggedTabIndex);

            if (tab == null) {
                draggedTabIndex = -1;
                return;
            }
            Tool<?> mainTool = tab.getMainHalfTab().getTool();
            HalfTab otherHalf = tab.getOtherHalfTab();
            Tool<?> otherTool = otherHalf == null ? null : otherHalf.getTool();
            
            TopLevel destinationWindow = MainWindow.getMouseMainWindow();
            if (destinationWindow == null) {
                // Tear off the tab into a new MainWindow

                if (tabs.size() > 1) {
                    removeTabAt(draggedTabIndex);

                    MainWindow newWindow = new MainWindow();
                    mainTool.go();

                    Tab newTab = newWindow.addTab(mainTool, otherTool);

                    newTab.setTitleTemplate(tab.getTitleTemplate());
                    newTab.setShortcut(tab.getShortcut());

                    newWindow.setVisible(true);
                }

            } else if (destinationWindow != mainTool.getToolPanel().getTopLevel()) {
                // Move the tab to a different MainWindow

                TopLevel currentMainWindow = mainTool.getToolPanel().getTopLevel();
                removeTabAt(draggedTabIndex);
                Tab newTab = destinationWindow.addTab(mainTool, otherTool);

                newTab.setTitleTemplate(tab.getTitleTemplate());
                newTab.setShortcut(tab.getShortcut());

                // Current window has no more tabs, so close it.
                if (tabs.size() == 0) {
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

            int targetTabIndex = tabbedPane.getUI().tabForCoordinate(tabbedPane, e.getX(), e.getY());

            if (targetTabIndex != -1 && targetTabIndex != draggedTabIndex) {

                boolean isForwardDrag = targetTabIndex > draggedTabIndex;
                int newIndex = draggedTabIndex + (isForwardDrag ? 1 : -1);

                Tab tab = getTab(draggedTabIndex);
                removeTabAt(draggedTabIndex);
                add(tab, newIndex);

                draggedTabIndex = newIndex;
                setSelectedIndex(draggedTabIndex);
            }
        }
    }

}
