package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.List;

public class TabNotifier
{
    private static List<TabListener> tabListeners = new ArrayList<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                int items = tabListeners.size();
                if (items > 0) {
                    System.err.println("Warniing. Possible memory leak. TabNotifier still has " + items + " items");
                    for (TabListener tl : listeners()) {
                        System.err.println("TabListener : " + tl);
                    }
                } else {
                    // System.err.println("TabNotifier is empty. Good!");
                }
            }
        });
    }

    public static void addTabListener(TabListener tl)
    {
        tabListeners.add(tl);
    }

    public static void removeTabListener(TabListener tl)
    {
        tabListeners.remove(tl);
    }

    /**
     * Copy the listeners to avoid concurrent modification exceptions.
     * 
     * @return All the current listeners
     */
    private static Iterable<TabListener> listeners()
    {
        return new ArrayList<>(tabListeners);
    }

    public static void fireAttached(ToolTab tab)
    {
        for (TabListener tl : listeners()) {
            tl.attachedTab(tab);
        }
    }

    public static void fireDetaching(ToolTab tab)
    {
        for (TabListener tl : listeners()) {
            tl.detachingTab(tab);
        }
    }

    public static void fireSelected(ToolTab tab)
    {
        for (TabListener tl : listeners()) {
            tl.selectedTab(tab);
        }
    }

    public static void fireDeselecting(ToolTab tab)
    {
        for (TabListener tl : listeners()) {
            tl.deselectingTab(tab);
        }
    }
    

    public static void fireChangedTitle(ToolTab tab)
    {
        for (TabListener tl : listeners()) {
            tl.changedTitle(tab);
        }
    }
}
