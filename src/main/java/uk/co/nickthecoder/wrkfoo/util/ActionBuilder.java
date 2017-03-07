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

public class ActionBuilder
{
    private final Object receiver;

    private Class<?> resourceClass;

    private JComponent component;

    private ExceptionHandler exceptionHandler;

    private String label;

    private Icon icon;

    private boolean visible = true;

    private boolean enable = true;

    private String tooltip;

    private String shortcut;

    private String methodName;

    private Method method;

    public ActionBuilder(Object receiver)
    {
        this.receiver = receiver;
        this.resourceClass = receiver.getClass();

        if (receiver instanceof JFrame) {
            component((JFrame) receiver);
        } else if (receiver instanceof JComponent) {
            component((JComponent) receiver);
        }
        if (receiver instanceof ExceptionHandler) {
            exceptionHandler = (ExceptionHandler) receiver;
        }
    }

    public ActionBuilder exceptionHandler(ExceptionHandler handler)
    {
        this.exceptionHandler = handler;
        return this;
    }

    public ActionBuilder component(JFrame frame)
    {
        this.component = frame.getRootPane();
        return this;
    }

    public ActionBuilder component(JComponent component)
    {
        this.component = component;
        return this;
    }

    public ActionBuilder label(String label)
    {
        this.label = label;
        return this;
    }

    public ActionBuilder resources(Class<?> klass)
    {
        resourceClass = klass;
        return this;
    }

    public ActionBuilder icon(String iconName)
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

    public ActionBuilder hide()
    {
        visible = false;
        return this;
    }

    public ActionBuilder show(boolean value)
    {
        visible = value;
        return this;
    }

    public ActionBuilder enable(boolean value)
    {
        enable = value;
        return this;
    }

    public ActionBuilder disable()
    {
        enable = false;
        return this;
    }

    public ActionBuilder tooltip(String text)
    {
        tooltip = text;
        return this;
    }

    public ActionBuilder shortcut(String shortcut)
    {
        this.shortcut = shortcut;
        return this;
    }

    private static final Class<?>[] EMPTY_SIGNATURE = {};
    private static final Object[] EMPTY_VALUES = {};

    public ActionBuilder action(String methodName)
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
    public ActionBuilder name(String name)
    {
        icon(name + ".png");
        action("on" + name.substring(0, 1).toUpperCase() + name.substring(1));

        return this;
    }

    public JButton buildButton()
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

        result.setVisible(visible);
        result.setEnabled(enable);
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
        final ExceptionHandler handler = this.exceptionHandler;
        Action action = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                try {
                    theMethod.invoke(receiver, EMPTY_VALUES);
                } catch (Throwable e) {
                    if (handler != null) {
                        handler.handleException(e);
                    }
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
        this.visible = true;
        this.enable = true;
    }
}
