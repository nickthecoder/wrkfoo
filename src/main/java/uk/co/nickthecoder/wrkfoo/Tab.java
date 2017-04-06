package uk.co.nickthecoder.wrkfoo;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import uk.co.nickthecoder.jguifier.util.Util;

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

    private HalfTab halfTab;

    public Tab(Tool<?> tool)
    {
        halfTab = new HalfTab(this, tool);
    }

    public HalfTab getHalfTab()
    {
        return halfTab;
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
        return titleTemplate.replaceAll("%t", halfTab.getTool().getShortTitle());
    }

    public JPanel getPanel()
    {
        return halfTab.getPanel();
    }

    public MainTabs getMainTabs()
    {
        return mainTabs;
    }

    void setMainTabs(MainTabs value)
    {
        if (value == null) {
            halfTab.detach();
        }
        mainTabs = value;
        if (value != null) {
            halfTab.attach(halfTab.getTool());
        }
    }

    public void select()
    {
        getMainTabs().setSelectedTab(this);
    }

}
