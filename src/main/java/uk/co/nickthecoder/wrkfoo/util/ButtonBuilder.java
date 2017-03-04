package uk.co.nickthecoder.wrkfoo.util;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

public class ButtonBuilder
{
    private Object receiver;

    private Class<?> resourceClass;

    private JComponent component;

    private String label;

    private Icon icon;

    private String tooltip;

    private String shortcut;

    private String methodName;

    private Method method;

    public ButtonBuilder(Object receiver)
    {
        this.receiver = receiver;
        this.resourceClass = receiver.getClass();

        if (receiver instanceof JFrame) {
            component((JFrame) receiver);
        } else if (receiver instanceof JComponent) {
            component((JComponent) receiver);
        }
    }

    public ButtonBuilder component(JFrame frame)
    {
        this.component = frame.getRootPane();
        return this;
    }

    public ButtonBuilder component(JComponent component)
    {
        this.component = component;
        return this;
    }

    public ButtonBuilder label(String label)
    {
        this.label = label;
        return this;
    }

    public ButtonBuilder resources(Class<?> klass)
    {
        resourceClass = klass;
        return this;
    }

    public ButtonBuilder icon(String iconName)
    {
        try {
            Image image = ImageIO.read(resourceClass.getResource(iconName));
            this.icon = new ImageIcon(image);
        } catch (Exception e) {
            if (label == null) {
                label = iconName;
            }
        }
        return this;
    }

    public ButtonBuilder tooltip(String text)
    {
        tooltip = text;
        return this;
    }

    public ButtonBuilder shortcut(String shortcut)
    {
        this.shortcut = shortcut;
        return this;
    }

    private static final Class<?>[] EMPTY_SIGNATURE = {};
    private static final Object[] EMPTY_VALUES = {};

    public ButtonBuilder action(String methodName)
    {
        this.methodName = methodName;
        try {
            method = receiver.getClass().getMethod(methodName, EMPTY_SIGNATURE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Uses the name to look for an icon, and as the method name.
     * The method name will be "on" + uppercasefirstletter(name)
     * The icon will be name + ".png"
     * 
     * @param name
     * @return this
     */
    public ButtonBuilder name(String name)
    {
        icon(name + ".png");
        action("on" + name.substring(0, 1).toUpperCase() + name.substring(1));

        return this;
    }

    public JButton build()
    {
        JButton result = new JButton();

        if (label != null)
            result.setText(label);
        if (icon != null)
            result.setIcon(icon);
        if (tooltip != null) {
            if (shortcut == null) {
                result.setToolTipText(tooltip);
            } else {
                result.setToolTipText(tooltip + "(" + shortcut.replace(' ', '+') + ")");
            }
        }

        if (method != null) {
            Action action = createAction();
            result.addActionListener(action);

            mapShortcut(action);
        }

        reset();

        return result;
    }

    public void buildShortcut()
    {
        if (method != null) {
            mapShortcut(createAction());
        }
        reset();
    }

    private Action createAction()
    {
        final Method theMethod = this.method;
        final String receiverClassName = receiver.getClass().getName() + "." + this.methodName;

        Action action = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                try {
                    theMethod.invoke(receiver, EMPTY_VALUES);
                } catch (RuntimeException re) {
                    System.err.println("Button Builder failed calling method " + receiverClassName);
                    throw re;
                } catch (Exception e) {
                    System.err.println("Button Builder failed calling method " + receiverClassName);
                    throw new RuntimeException(e);
                }
            }
        };
        return action;
    }

    private void mapShortcut(Action action)
    {
        if (shortcut != null) {
            InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = component.getActionMap();

            KeyStroke keyStroke = KeyStroke.getKeyStroke(shortcut);
            inputMap.put(keyStroke, methodName);
            actionMap.put(methodName, action);
        }
    }

    private void reset()
    {
        // Reset the builder, so that it can be used again.
        this.label = null;
        this.icon = null;
        this.shortcut = null;
        this.tooltip = null;
        this.method = null;
        this.methodName = null;

    }
}
