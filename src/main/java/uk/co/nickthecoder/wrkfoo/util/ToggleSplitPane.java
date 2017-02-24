package uk.co.nickthecoder.wrkfoo.util;

import java.awt.Component;
import java.awt.KeyboardFocusManager;

import javax.swing.JSplitPane;
import javax.swing.UIManager;

public class ToggleSplitPane extends JSplitPane
{
    private int loc = 0;

    boolean hideLeft;
    boolean hidden = false;

    public ToggleSplitPane(int or, boolean con, Component left, Component right, boolean hideLeft)
    {
        super(or, con, left, right);
        this.hideLeft = hideLeft;

        this.hidden = !(hideLeft ? left.isVisible() : right.isVisible());
    }

    public Component hider()
    {
        return this.hideLeft ? getLeftComponent() : getRightComponent();
    }
    public Component other()
    {
        return this.hideLeft ? getRightComponent() : getLeftComponent();
    }

    public void toggle()
    {
        hidden = !hidden;

        if (hidden) {
            loc = getDividerLocation();
            setDividerSize(0);
            hider().setVisible(false);
            other().requestFocusInWindow();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
        } else {
            hider().setVisible(true);
            hider().requestFocusInWindow();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
            setDividerLocation(loc);
            setDividerSize((Integer) UIManager.get("SplitPane.dividerSize"));
        }
    }

    public void toggle(boolean show)
    {
        if (show != hidden) {
            toggle();
        }
    }
}
