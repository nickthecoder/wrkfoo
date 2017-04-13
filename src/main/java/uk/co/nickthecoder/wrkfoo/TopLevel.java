package uk.co.nickthecoder.wrkfoo;

import java.awt.Component;
import java.awt.Window;

import javax.swing.SwingUtilities;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.util.ExceptionHandler;

/**
 * The top level GUI component, this is usually a {@link MainWindow}.
 */
public interface TopLevel extends ExceptionHandler
{

    public static TopLevel getTopLevel(Component component)
    {
        Util.assertIsEDT();
        Window window = SwingUtilities.getWindowAncestor(component);
        // if (window == null) {
        // System.err.println("Failed to find window for component : " + component);
        // }
        return (TopLevel) window;
    }

    public Tab insertTab(Tool<?> tool, boolean prompt);

    public Tab addTab(Tool<?> tool);

    public Tab addTab(Tool<?> tool, Tool<?> otherTool);

    public void setVisible(boolean value);
}
