package uk.co.nickthecoder.wrkfoo.util;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JSplitPane;
import javax.swing.UIManager;

public class HidingSplitPane extends JSplitPane
{
    private static final long serialVersionUID = 1L;

    private int loc = 0;

    State state;

    public enum State
    {
        LEFT, RIGHT, BOTH
    };

    public HidingSplitPane(int or, boolean con, Component left, Component right)
    {
        super(or, con, left, right);
        this.state = State.BOTH;
        FocusAdapter listener = new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
            }
        };
        left.addFocusListener(listener);
        right.addFocusListener(listener);
    }

    public void setState(State newState)
    {
        if (this.state == newState) {
            return;
        }

        if (newState == State.BOTH) {
            // From LEFT or RIGHT to BOTH
            // Show the hidden component, resetting the divider position and size.
            Component makeVisibleComponent = this.state == State.LEFT ? getRightComponent() : getLeftComponent();
            makeVisibleComponent.setVisible(true);
            setDividerLocation(loc);
            setDividerSize((Integer) UIManager.get("SplitPane.dividerSize"));

        } else {
            if (this.state == State.BOTH) {
                // From BOTH to LEFT or RIGHT
                // Hide a component, and remember the divider position and size
                loc = getDividerLocation();
                setDividerSize(0);
                Component makeInvisibleComponent = newState == State.LEFT ? getRightComponent() : getLeftComponent();
                makeInvisibleComponent.setVisible(false);

            } else {
                // From LEFT to RIGHT or from RIGHT to LEFT.
                // Swap the visibility of left & right, but don't do anything with the divider
                Component makeInvisibleComponent = newState == State.LEFT ? getRightComponent() : getLeftComponent();
                Component makeVisibleComponent = this.state == State.LEFT ? getRightComponent() : getLeftComponent();
                makeInvisibleComponent.setVisible(true);
                makeVisibleComponent.setVisible(false);
            }
        }

        this.state = newState;
    }

    public void showLeft()
    {
        if (this.state == State.RIGHT) {
            setState(State.BOTH);
        }
    }

    public void showRight()
    {
        if (this.state == State.LEFT) {
            setState(State.BOTH);
        }
    }

    /**
     * Toggles the visibility of the left component.
     * 
     * @return The component which just became visible
     */
    public Component toggleLeft()
    {
        setState(this.state == State.BOTH ? State.LEFT : State.BOTH);

        return this.state == State.LEFT ? getLeftComponent() : getRightComponent();
    }

    /**
     * Toggles the visibility of the left component.
     */
    public Component toggleRight()
    {
        setState(this.state == State.BOTH ? State.RIGHT : State.BOTH);

        return this.state == State.RIGHT ? getRightComponent() : getLeftComponent();
    }

    public State getState()
    {
        return state;
    }
}
