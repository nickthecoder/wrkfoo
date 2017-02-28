package uk.co.nickthecoder.wrkfoo.util;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ButtonBuilder
{
    private Object receiver;

    private Class<?> resourceClass;

    private String label;

    private Icon icon;

    private String tooltip;

    private Method method;

    public ButtonBuilder(Object receiver)
    {
        this.receiver = receiver;
        this.resourceClass = receiver.getClass();
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

    private static final Class<?>[] EMPTY_SIGNATURE = {};
    private static final Object[] EMPTY_VALUES = {};

    public ButtonBuilder action(String methodName)
    {
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
        icon( name + ".png" );
        action( "on" + name.substring(0, 1).toUpperCase() + name.substring(1));
        
        return this;
    }

    public JButton build()
    {
        JButton result = new JButton();

        if (label != null)
            result.setText(label);
        if (icon != null)
            result.setIcon(icon);
        if (tooltip != null)
            result.setToolTipText(tooltip);

        if (method != null) {
            final Method theMethod = this.method;
            result.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent event)
                {
                    try {
                        theMethod.invoke(receiver, EMPTY_VALUES);
                    } catch (RuntimeException re) {
                        throw re;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        // Reset the builder, so that it can be used again.
        this.label = null;
        this.icon = null;
        this.tooltip = null;
        this.method = null;

        return result;
    }
}
