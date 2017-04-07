package uk.co.nickthecoder.wrkfoo.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Timer;

import uk.co.nickthecoder.jguifier.util.Util;

public class ComponentUpdateManager
{
    public static int DEFAULT_PERIOD = 500; // Half a second
    
    private Set<WeakReference<ComponentUpdater>> updaters;

    private Set<ComponentUpdater> pendingRemoval;

    private Set<WeakReference<ComponentUpdater>> weakPendingRemoval;

    private Set<ComponentUpdater> pendingAdditions;

    private Timer timer;

    public static ComponentUpdateManager instance;
    
    public static ComponentUpdateManager getInstance()
    {
        if ( instance == null ) {
            instance = new ComponentUpdateManager();
            instance.startTImedUpdates(DEFAULT_PERIOD);
        }
        
        return instance;
    }
    
    public ComponentUpdateManager()
    {
        updaters = new HashSet<>();
        pendingRemoval = new HashSet<>();
        weakPendingRemoval = new HashSet<>();
        pendingAdditions = new HashSet<>();
    }

    public void updateAll()
    {
        Util.assertIsEDT();

        for (ComponentUpdater updater : pendingAdditions) {
            updaters.add(new WeakReference<ComponentUpdater>(updater));
        }
        
        for (WeakReference<ComponentUpdater> weakUpdater : updaters) {
            ComponentUpdater updater = weakUpdater.get();
            if (updater == null) {
                weakPendingRemoval.add(weakUpdater);
            } else {
                if (pendingRemoval.contains(updater)) {
                    weakPendingRemoval.add(weakUpdater);
                } else {
                    update(updater);
                }
            }
        }
        updaters.removeAll(weakPendingRemoval);
    }

    private void update(ComponentUpdater update)
    {
        update.update();
    }

    public void add(ComponentUpdater update)
    {
        pendingAdditions.add(update);
    }

    public void remove(ComponentUpdater update)
    {
        pendingRemoval.add(update);
    }

    public void startTImedUpdates(int periodMillis)
    {
        if (timer != null) {
            throw new RuntimeException("Timer already started");
        }
        
        ActionListener al = new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                updateAll();
            }
        };
        timer = new Timer(periodMillis, al);
        timer.start();
    }

    public void stopTImedUpdates()
    {
        timer.stop();
    }
}
