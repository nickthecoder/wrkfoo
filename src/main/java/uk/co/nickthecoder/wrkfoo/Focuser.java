package uk.co.nickthecoder.wrkfoo;

import java.awt.Component;
import java.util.Date;

import javax.swing.SwingUtilities;

/**
 * Keyboard focus is important, and tricky in this application. The focus should always be somewhere
 * sensible but sometimes two different components request the focus nearly simultaneously, because
 * neither knows about the other. This class aims to help this situation by giving a priority to
 * each focus request, thus focusing on the 'correct' component in those situations.
 * It also has a logging ability, which greatly aids with debugging.
 */
public class Focuser
{
    private static int focusImportance = -1;

    private static Component focusComponent;

    private static long focusTime;

    private static String focusDescription;

    /**
     * The time in milliseconds after which less important focus events actually have an effect.
     */
    public static int TOO_SOON_INTERVAL = 50;

    private static final boolean debug = false;

    public static void log(String message)
    {
        if (debug) {
            System.out.println(message);
        }
    }

    /**
     * Focuses on the component invoking later, using SwingUtilities.invokeLater.
     * If a component is already requested to be focused, and before it has been carried out,
     * then the less important will be ignored.
     * If a component has very recently been focused, and then a request to focus with lower importance
     * will be ignored
     * 
     * @param c
     *            The component to focus on
     * @param description
     *            A description of why/what is being focused on, just to help debugging
     * @param importance
     *            Range 0..10, 10 being the most important
     */
    public static void focusLater(String description, Component c, int importance)
    {
        if (c == null) {
            log("Ignoring null component. Description : " + description);
            return;
        }

        // Note. focusComponent is null, when there is nothing waiting, but focusImportance is NOT reset back to -1.
        if ((focusComponent == null) || (importance > focusImportance)) {
            if (focusComponent == null) {
                // No focus pending.
                long now = new Date().getTime();
                if ((importance < focusImportance) && (now - focusTime < TOO_SOON_INTERVAL)) {
                    // Ignore a lower importance soon after a high importance.
                    log("Skipping - too soon");
                    return;
                }

            } else {
                // There is a focus pending
                if (importance < focusImportance) {
                    // Ignore lower importance
                    log("Ignoring low importance " + description + " for " + focusDescription);
                    return;
                } else {
                    // Ok, lets replace the pending one with this higher importance one.
                    log("Doing " + description + " instead of " + focusDescription);
                }
            }

            Component old = focusComponent;

            focusComponent = c;
            focusImportance = importance;
            focusDescription = description;

            if (old == null) {
                // log("Invoking later");
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        log("Focusing NOW. " + focusDescription);
                        focusComponent.requestFocusInWindow();
                        focusComponent = null;
                        focusTime = new Date().getTime();
                    }
                });
            }
        } else {
            log("Ignoring " + description + ". Doing this instead : " + focusDescription);
        }
    }
}
