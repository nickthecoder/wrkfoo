package uk.co.nickthecoder.wrkfoo;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

/**
 * The isn't a GUI component, it only hold the data associated with one of the tabs in the {@link MainTabs}.
 * It has a {@link Tool}, but over time, the tool will change. The history of all of the tools, and the
 * Tool's Task's parameters are stored in a {@link History}, which allows the user to go backwards and
 * forwards similar to web browser's back and forward buttons.
 * 
 */
public class Tab
{
    private MainTabs mainTabs;

    private String titleTemplate = "%t";

    private String shortcut;

    private HalfTab mainHalfTab;

    private HalfTab otherHalfTab;

    private HidingSplitPane splitPane;

    public Tab(Tool<?> tool)
    {
        this(tool, null);
    }

    public Tab(Tool<?> mainTool, Tool<?> otherTool)
    {
        mainHalfTab = new HalfTab(this, mainTool);
        if (otherTool != null) {
            otherHalfTab = new HalfTab(this, otherTool);
        }

        JComponent otherComponent = otherTool == null ? new JLabel("Nothing") : otherHalfTab.getComponent();

        splitPane = new HidingSplitPane(
            HidingSplitPane.HORIZONTAL_SPLIT,
            true,
            mainHalfTab.getComponent(),
            otherComponent);

        splitPane.setState(otherTool == null ? HidingSplitPane.State.LEFT : HidingSplitPane.State.BOTH);
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.5);
    }

    public HalfTab getMainHalfTab()
    {
        return mainHalfTab;
    }

    public HalfTab getOtherHalfTab()
    {
        return otherHalfTab;
    }

    public void split(Tool<?> tool)
    {
        if (otherHalfTab != null) {
            otherHalfTab.detach();
        }

        otherHalfTab = new HalfTab(this, tool);
        otherHalfTab.attach(otherHalfTab.getTool());
        otherHalfTab.go(tool);
        splitPane.setRightComponent(otherHalfTab.getComponent());
        splitPane.setState(HidingSplitPane.State.BOTH);
        splitPane.setDividerLocation(0.5);
    }

    public void unsplit()
    {
        if (otherHalfTab == null) {
            return;
        }

        otherHalfTab.detach();
        otherHalfTab = null;
        splitPane.setRightComponent(new JLabel("unsplit"));
        splitPane.setState(HidingSplitPane.State.LEFT);

        // Focus on the main component's results.
        Focuser.focusLater("Tab.unsplit. Result's component",
            mainHalfTab.getTool().getResultsPanel().getFocusComponent(), 7);

        mainHalfTab.getTool().getResultsPanel().getFocusComponent();
    }

    public void setTitleTemplate(String value)
    {
        titleTemplate = value;
    }

    public String getShortcut()
    {
        return shortcut;
    }

    public void setShortcut(String value)
    {
        if (Util.equals(value, shortcut)) {
            return;
        }

        InputMap inputMap = getMainTabs().getComponent().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getMainTabs().getComponent().getActionMap();

        if (shortcut != null) {
            String actionMapKey = "selectTab-" + shortcut;
            KeyStroke keyStroke = KeyStroke.getKeyStroke(shortcut);
            inputMap.remove(keyStroke);
            actionMap.remove(actionMapKey);
        }

        shortcut = value;
        String actionMapKey = "selectTab-" + shortcut;

        if (!Util.empty(shortcut)) {

            KeyStroke keyStroke = KeyStroke.getKeyStroke(shortcut);
            inputMap.put(keyStroke, actionMapKey);
            actionMap.put(actionMapKey, new AbstractAction()
            {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    getMainTabs().setSelectedTab(Tab.this);
                }
            });
        }
    }

    public String getTitleTemplate()
    {
        return titleTemplate;
    }

    public String getTitle()
    {
        return titleTemplate.replaceAll("%t", mainHalfTab.getTool().getShortTitle());
    }

    public JComponent getComponent()
    {
        return splitPane;
    }

    public MainTabs getMainTabs()
    {
        return mainTabs;
    }

    void setMainTabs(MainTabs value)
    {
        if (value == null) {
            mainHalfTab.detach();
        }
        mainTabs = value;
        if (value != null) {
            mainHalfTab.attach(mainHalfTab.getTool());

            if (otherHalfTab != null) {
                otherHalfTab.attach(otherHalfTab.getTool());
            }
        }
    }

    public void select()
    {
        getMainTabs().setSelectedTab(this);
    }

}
