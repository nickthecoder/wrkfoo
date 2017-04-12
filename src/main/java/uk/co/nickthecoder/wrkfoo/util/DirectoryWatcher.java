package uk.co.nickthecoder.wrkfoo.util;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectoryWatcher
{
    public static int DEFAULT_PERIOD = 1000;

    private static DirectoryWatcher instance;

    private Map<FileSystem, WatchService> watchServiceByFileSystem;

    private Map<Path, List<WeakReference<DirectoryListener>>> listenersByPath;

    public static DirectoryWatcher getInstance()
    {
        if (instance == null) {
            instance = new DirectoryWatcher();
        }
        return instance;
    }

    public DirectoryWatcher()
    {
        listenersByPath = new HashMap<>();
        watchServiceByFileSystem = new HashMap<>();
    }

    public void register(File directory, DirectoryListener dl) throws IOException
    {
        register(directory.toPath(), dl);
    }

    public void register(Path directory, DirectoryListener dl) throws IOException
    {
        List<WeakReference<DirectoryListener>> list = listenersByPath.get(directory);
        if (list == null) {
            list = new ArrayList<>();
            listenersByPath.put(directory, list);
            directory.register(getWatchService(directory), ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        }
        list.add(new WeakReference<DirectoryListener>(dl));
    }

    public void unregister(DirectoryListener dl)
    {
        for (List<WeakReference<DirectoryListener>> list : listenersByPath.values()) {
            for (WeakReference<DirectoryListener> wr : list) {
                if (wr.get() == dl) {
                    list.remove(wr);
                    return;
                }
            }
        }
    }

    public void unregister(Path directory, DirectoryListener dl)
    {
        List<WeakReference<DirectoryListener>> list = listenersByPath.get(directory);
        if (list == null) {
            return;
        }
        for (WeakReference<DirectoryListener> wr : list) {
            if (wr.get() == dl) {
                list.remove(wr);
                break;
            }
        }
        if (list.isEmpty()) {
            listenersByPath.remove(list);
        }
    }

    private WatchService getWatchService(Path path) throws IOException
    {
        FileSystem filesystem = path.getFileSystem();
        WatchService watchService = watchServiceByFileSystem.get(filesystem);
        if (watchService == null) {
            watchService = filesystem.newWatchService();
            watchServiceByFileSystem.put(filesystem, watchService);

            startPolling(watchService, DEFAULT_PERIOD);
        }

        return watchService;
    }

    private void startPolling(final WatchService watcher, int millis)
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true) {
                    poll(watcher);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void poll(WatchService watcher)
    {
        WatchKey key;
        try {
            key = watcher.take();
        } catch (InterruptedException x) {
            return;
        }

        Path directory = (Path) key.watchable();

        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();

            if (kind == OVERFLOW) {
                continue;
            }

            @SuppressWarnings("unchecked")
            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path path = ev.context();

            if (!notifyListeners(directory, path)) {
                key.cancel();
                return;
            }
            break;
        }

        boolean valid = key.reset();
        if (!valid) {
            listenersByPath.remove(directory);
        }
    }

    private boolean notifyListeners(Path directory, Path path)
    {
        List<WeakReference<DirectoryListener>> list = listenersByPath.get(directory);
        if (list == null) {
            return false;
        }
        if (list.isEmpty()) {
            listenersByPath.remove(directory);
            return false;
        }

        for (WeakReference<DirectoryListener> wr : list) {
            DirectoryListener dl = wr.get();
            if (dl == null) {
                list.remove(wr);
                // Recurse to prevent concurrent modification exception.
                return notifyListeners(directory, path);
            }
            try {
                dl.directoryChanged(directory, path);
            } catch (Exception e) {
                e.printStackTrace();
                list.remove(wr);
                // Recurse to prevent concurrent modification exception.
                return notifyListeners(directory, path);
            }
        }
        return true;
    }
}
