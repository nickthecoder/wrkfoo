package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

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

    public static void nowOrLater(Runnable r)
    {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    public static void fireAttached(final ToolTab tab)
    {
        nowOrLater(new Runnable()
        {
            public void run()
            {
                for (TabListener tl : listeners()) {
                    tl.attachedTab(tab);
                }
            }
        });
    }

    public static void fireDetaching(final ToolTab tab)
    {
        nowOrLater(new Runnable()
        {
            public void run()
            {
                for (TabListener tl : listeners()) {
                    tl.detachingTab(tab);
                }
            }
        });
    }

    public static void fireSelected(final ToolTab tab)
    {
        nowOrLater(new Runnable()
        {
            public void run()
            {
                for (TabListener tl : listeners()) {
                    tl.selectedTab(tab);
                }
            }
        });
    }

    public static void fireDeselecting(final ToolTab tab)
    {
        nowOrLater(new Runnable()
        {
            public void run()
            {
                for (TabListener tl : listeners()) {
                    tl.deselectingTab(tab);
                }
            }
        });
    }

    public static void fireChangedTitle(final ToolTab tab)
    {
        nowOrLater(new Runnable()
        {
            public void run()
            {
                for (TabListener tl : listeners()) {
                    tl.changedTitle(tab);
                }

            }
        });
    }
}
