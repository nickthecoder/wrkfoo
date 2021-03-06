package uk.co.nickthecoder.wrkfoo.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

import uk.co.nickthecoder.wrkfoo.util.ActionShortcuts.Shortcuts;

public class ActionBuilder
{
    private final Object receiver;

    private Class<?> resourceClass;

    private JComponent component;

    private int condition = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;

    private ExceptionHandler exceptionHandler;

    private String label;

    private Icon icon;

    private boolean visible = true;

    private boolean enable = true;

    private String tooltip;

    private String actionName;

    public ActionBuilder(Object receiver)
    {
        this.receiver = receiver;
        this.resourceClass = receiver.getClass();

        if (receiver instanceof JFrame) {
            Component content = ((JFrame) receiver).getContentPane();
            if (content instanceof JComponent) {
                component((JComponent) content);
            }

        } else if (receiver instanceof JDialog) {
            Component content = ((JDialog) receiver).getContentPane();
            if (content instanceof JComponent) {
                component((JComponent) content);
            }

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

    public ActionBuilder component(JComponent component)
    {
        this.component = component;
        return this;
    }

    /**
     * The default is {@link JComponent#WHEN_ANCESTOR_OF_FOCUSED_COMPONENT}
     * 
     * @param condition
     *            One of
     *            {@link JComponent#WHEN_ANCESTOR_OF_FOCUSED_COMPONENT}
     *            {@link JComponent#WHEN_FOCUSED}
     *            {@link JComponent#WHEN_IN_FOCUSED_WINDOW}
     * @return this
     */
    public ActionBuilder condition(int condition)
    {
        this.condition = condition;
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

    private static final Class<?>[] EMPTY_SIGNATURE = {};
    private static final Object[] EMPTY_VALUES = {};

    public Method getMethod()
    {
        try {
            return receiver.getClass().getMethod(methodName, EMPTY_SIGNATURE);
        } catch (Exception e) {
            throw new RuntimeException("Failed to method : " + methodName);
        }
    }

    public ActionBuilder method(String methodName)
    {
        this.methodName = methodName;
        return this;
    }

    private String methodName;

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
        actionName = name;
        String lastPart = actionName;
        int dot = lastPart.lastIndexOf('.');
        if (dot >= 0) {
            lastPart = lastPart.substring(dot + 1);
        }
        icon(lastPart + ".png");
        method("on" + lastPart.substring(0, 1).toUpperCase() + lastPart.substring(1));

        return this;
    }

    public JMenuItem buildMenuItem()
    {
        JMenuItem result = new JMenuItem(label);

        // Set the menu accelerator to the first keystroke found.
        Shortcuts shortcuts = ActionShortcuts.instance.get(actionName);
        if (shortcuts != null) {

            for (KeyStroke keyStroke : shortcuts.keyStrokes) {
                result.setAccelerator(keyStroke);
                break;
            }
        }

        if (methodName != null) {
            Action action = createAction();
            result.addActionListener(action);

            mapShortcut(action);
        }

        return result;
    }

    public JToggleButton buildToggleButton()
    {
        final JToggleButton result = new JToggleButton();
        buildButton(result);

        if (methodName != null) {
            final Action action = createAction();
            result.addActionListener(action);

            Action toggleAction = new AbstractAction()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    result.setSelected(!result.isSelected());
                    action.actionPerformed(e);
                }
            };
            mapShortcut(toggleAction);
        }

        reset();
        return result;
    }

    public JButton buildButton()
    {
        JButton result = new JButton();
        buildButton(result);

        if (methodName != null) {
            Action action = createAction();
            result.addActionListener(action);
            mapShortcut(action);
        }

        reset();
        return result;
    }

    private void buildButton(AbstractButton result)
    {

        if (label != null) {
            result.setText(label);
        }
        if (icon != null) {
            result.setIcon(icon);
        }

        if (tooltip != null) {
            result.setToolTipText(tooltip + ActionShortcuts.instance.tooltipSuffix(actionName));
        }

        result.setVisible(visible);
        result.setEnabled(enable);
    }

    public JCheckBox buildCheckBox()
    {
        JCheckBox result = new JCheckBox();

        if (label != null) {
            result.setText(label);
        }
        if (icon != null) {
            result.setIcon(icon);
        }

        if (tooltip != null) {
            result.setToolTipText(tooltip + ActionShortcuts.instance.tooltipSuffix(actionName));
        }

        if (methodName != null) {
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
        if (methodName != null) {
            mapShortcut(createAction());
        }
        reset();
    }

    private Action createAction()
    {
        final Method theMethod = this.getMethod();
        final ExceptionHandler handler = this.exceptionHandler;
        Action action = new AbstractAction()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event)
            {
                try {
                    theMethod.invoke(receiver, EMPTY_VALUES);
                    ComponentUpdateManager.getInstance().updateAll();
                } catch (Throwable e) {
                    if (handler != null) {
                        handler.handleException(e);
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        };
        return action;
    }

    private void mapShortcut(Action action)
    {
        Shortcuts shortcuts = ActionShortcuts.instance.get(actionName);
        if (shortcuts == null) {
            System.err.println("Warning no shortcuts found for " + actionName);
            return;
        }

        ActionMap actionMap = component.getActionMap();
        actionMap.put(actionName, action);

        InputMap inputMap = component.getInputMap(condition);

        for (KeyStroke keyStroke : shortcuts.keyStrokes) {
            inputMap.put(keyStroke, actionName);
        }
    }

    private void reset()
    {
        // Reset the builder, so that it can be used again.
        this.label = null;
        this.icon = null;
        this.tooltip = null;
        this.methodName = null;
        this.actionName = null;
        this.visible = true;
        this.enable = true;
    }
}
