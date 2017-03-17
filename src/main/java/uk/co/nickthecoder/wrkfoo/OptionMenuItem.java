package uk.co.nickthecoder.wrkfoo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JMenuItem;
import javax.swing.UIManager;

import uk.co.nickthecoder.wrkfoo.option.Option;

/**
 * A menu item displaying an option's label, as well as its code.
 * The code is right aligned, in a similar manner to keyboard shortcuts in regular menus.
 */
public class OptionMenuItem extends JMenuItem
{
    private static Color armedBackground = UIManager.getColor("Menu.selectionBackground");
    private static Color background = UIManager.getColor("Menu.background");

    private static Color armedForeground = UIManager.getColor("Menu.selectionForeground");
    private static Color foreground = UIManager.getColor("Menu.foreground");

    public final Option option;

    public OptionMenuItem(Option option)
    {
        super("");
        this.option = option;
    }

    public Dimension getPreferredSize()
    {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        if (! isVisible()) {
            return new Dimension(0,0);
        }

        FontMetrics fm = getGraphics().getFontMetrics();
        // 10 is an arbitrary gap between the label and the code.
        int width = fm.stringWidth(option.getLabel()) + fm.stringWidth(option.getCode()) + 10;
        int height = fm.getHeight();

        Insets insets = getInsets();
        return new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom);
    }

    public void paint(Graphics g)
    {
        Graphics2D graphics2d = (Graphics2D) g;
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(isArmed() ? armedBackground : background);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(isArmed() ? armedForeground : foreground);

        Insets insets = getInsets();

        int y = g.getFontMetrics().getAscent() + insets.bottom;

        g.drawString(option.getLabel(), insets.left, y);

        int x = getWidth() - g.getFontMetrics().stringWidth(option.getCode()) - insets.right;
        g.drawString(option.getCode(), x, y);
    }

}
