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
        if ( !tabListeners.contains(tl)) {
            System.err.println( "Warning: Removing TabListener that does not exist.");
        }
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

    public static void fireAttached(final Tab tab)
    {
        nowOrLater(new Runnable()
        {
            public void run()
            {
                for (TabListener tl : listeners()) {
                    tl.attached(tab);
                }
            }
        });
    }

    public static void fireDetaching(final Tab tab)
    {
        nowOrLater(new Runnable()
        {
            public void run()
            {
                for (TabListener tl : listeners()) {
                    tl.detaching(tab);
                }
            }
        });
    }

    public static void fireAttached(final HalfTab halfTab)
    {
        nowOrLater(new Runnable()
        {
            public void run()
            {
                for (TabListener tl : listeners()) {
                    tl.attached(halfTab);
                }
            }
        });
    }

    public static void fireDetaching(final HalfTab halfTab)
    {
        nowOrLater(new Runnable()
        {
            public void run()
            {
                for (TabListener tl : listeners()) {
                    tl.detaching(halfTab);
                }
            }
        });
    }

    public static void fireSelected(final Tab tab)
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

    public static void fireDeselecting(final Tab tab)
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

    public static void fireChangedTitle(final Tab tab)
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
