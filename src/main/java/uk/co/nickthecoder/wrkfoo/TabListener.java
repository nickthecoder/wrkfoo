package uk.co.nickthecoder.wrkfoo;

/**
 * Notified when a ToolTab changes state; when it is attached, detached etc.
 */
public interface TabListener
{
    /**
     * Called AFTER a tab has been added to the TabbedPane
     * @param tab
     */
    public void attachedTab( ToolTab tab );

    /**
     * Called just BEFORE a tab will be removed from the TabbedPane
     * @param tab
     */
    public void detachingTab( ToolTab tab );
    
    /**
     * Called AFTER a tab has been made the currently selected tab in the TabbedPane
     * @param tab
     */
    public void selectedTab( ToolTab tab );
    
    /**
     * Called just before a tab will no longer be the currently selected tab in the TabbedPane
     * @param tab
     */
    public void deselectingTab( ToolTab tab );
}
