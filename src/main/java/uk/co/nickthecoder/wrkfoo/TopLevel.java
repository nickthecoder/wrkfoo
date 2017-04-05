package uk.co.nickthecoder.wrkfoo;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import uk.co.nickthecoder.wrkfoo.util.ExceptionHandler;

/**
 * The top level GUI component, this is usually a {@link MainWindow}.
 */
public interface TopLevel extends ExceptionHandler
{

    public static TopLevel getTopLevel(Component component)
    {
        Window window = SwingUtilities.getWindowAncestor(component);
        // if (window == null) {
        // System.err.println("Failed to find window for component : " + component);
        // }
        return (TopLevel) window;
    }
    
    public void addToolBar(JComponent toolBar);

    public void addStatusBar(JComponent statusBar);

    public ToolTab insertTab(Tool<?> tool, boolean prompt);

    public ToolTab addTab(Tool<?> tool);

    public void setVisible(boolean value);
}
