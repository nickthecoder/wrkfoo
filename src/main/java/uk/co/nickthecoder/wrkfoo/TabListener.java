package uk.co.nickthecoder.wrkfoo;

/**
 * Notified when a {@link Tab} changes state; when it is attached, detached etc.
 */
public interface TabListener
{
    /**
     * Called AFTER a tab has been added to the MainTabs
     * 
     * @param tab
     */
    public void attached(Tab tab);

    /**
     * Called just BEFORE a tab will be removed from the MainTabs
     * 
     * @param tab
     */
    public void detaching(Tab tab);
    
    /**
     * Called AFTER a HalfTab has been added to the MainTabs
     * 
     * @param tab
     */
    public void attached(HalfTab halfTab);

    /**
     * Called just BEFORE a HalfTab will be removed from the MainTabs
     * 
     * @param tab
     */
    public void detaching(HalfTab halfTab);

    /**
     * Called AFTER a tab has been made the currently selected tab in the MainTabs
     * 
     * @param tab
     */
    public void selectedTab(Tab tab);

    /**
     * Called just before a tab will no longer be the currently selected tab in the MainTabs
     * 
     * @param tab
     */
    public void deselectingTab(Tab tab);

    /**
     * Called when a Tool changes its title (long or short), so that the MainWindow and the MainTabs can
     * pick up the changes.
     * 
     * @param tab
     */
    public void changedTitle(Tab tab);
}
