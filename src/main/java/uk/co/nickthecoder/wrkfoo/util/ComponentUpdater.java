package uk.co.nickthecoder.wrkfoo.util;

/**
 * Updates the GUI Component in a 'idle'
 */
public interface ComponentUpdater
{
    /**
     * Updates the component
     * @return false if the updater is no longer required, and should be removed
     */
    public boolean update();
}
