package uk.co.nickthecoder.wrkfoo.util;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileWatcher
{
    private static FileWatcher instance;

    private Map<FileSystem, WatchService> watchServiceByFileSystem;

    private Map<Path, List<Entry>> entriesByDirectory;

    public static FileWatcher getInstance()
    {
        if (instance == null) {
            instance = new FileWatcher();
        }
        return instance;
    }

    public FileWatcher()
    {
        entriesByDirectory = new HashMap<>();
        watchServiceByFileSystem = new HashMap<>();
    }

    public void register(File file, FileListener listener) throws IOException
    {
        register(file.toPath(), listener);
    }

    public void register(Path file, FileListener listener) throws IOException
    {
        Path directory;
        if (Files.isDirectory(file)) {
            directory = file;
        } else {
            directory = file.getParent();
        }

        List<Entry> list = entriesByDirectory.get(directory);
        if (list == null) {
            list = new ArrayList<>();
            entriesByDirectory.put(directory, list);
            directory.register(getWatchService(directory), ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        }
        list.add(new Entry(listener, file));
    }

    public void unregister(FileListener listener)
    {
        for (List<Entry> list : entriesByDirectory.values()) {
            for (Entry entry : list) {
                if (entry.weakListener.get() == listener) {
                    list.remove(entry);
                    return;
                }
            }
        }
    }

    public void unregister(Path file, FileListener dl)
    {
        Path directory;
        if (Files.isDirectory(file)) {
            directory = file;
        } else {
            directory = file.getParent();
        }

        List<Entry> list = entriesByDirectory.get(directory);
        if (list == null) {
            return;
        }

        for (Entry entry : list) {
            if (entry.weakListener.get() == dl) {
                list.remove(entry);
                break;
            }
        }
        if (list.isEmpty()) {
            entriesByDirectory.remove(list);
        }
    }

    private WatchService getWatchService(Path directory) throws IOException
    {
        FileSystem filesystem = directory.getFileSystem();
        WatchService watchService = watchServiceByFileSystem.get(filesystem);
        if (watchService == null) {
            watchService = filesystem.newWatchService();
            watchServiceByFileSystem.put(filesystem, watchService);

            startPolling(watchService);
        }

        return watchService;
    }

    private void startPolling(final WatchService watcher)
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

        /*
         * I used this tutorial, but it seems broken, as it misses some changes :-(
         * https://docs.oracle.com/javase/tutorial/essential/io/notification.html
         *
         * We cannot reply on WatchService to tell us about ALL changes to the directory, because
         * we need to perform a reset in order to receive further changes. If the directory is changed AGAIN
         * before the reset is called, then we will not see the 2nd change. This is common, because
         * when saving documents, an application will often save to a temporary file, and then rename it to
         * the real file (e.g. gedit does this).
         * So instead, we keep track of each listener's file's last modified time-stamp, and check each listener of
         * this directory to see if their file has changed.
         * 
         * i.e. we ignore the result of key.pollEvents()
         */
        key.pollEvents();

        if (!notifyListeners(directory)) {
            key.cancel();
            return;
        }

        boolean valid = key.reset();
        if (!valid) {
            // Directory has been deleted, so remove all the listeners
            entriesByDirectory.remove(directory);
        }
    }

    private boolean notifyListeners(Path directory)
    {
        List<Entry> list = entriesByDirectory.get(directory);
        if (list == null) {
            return false;
        }
        if (list.isEmpty()) {
            entriesByDirectory.remove(directory);
            return false;
        }

        for (Entry entry : list) {
            FileListener listener = entry.weakListener.get();
            if (listener == null) {
                list.remove(entry);
                // Recurse to prevent concurrent modification exception.
                return notifyListeners(directory);
            }
            try {
                entry.check();
            } catch (Exception e) {
                e.printStackTrace();
                list.remove(entry);
                // Recurse to prevent concurrent modification exception.
                return notifyListeners(directory);
            }
        }
        return true;
    }

    private class Entry
    {
        private WeakReference<FileListener> weakListener;

        private Path path;

        long lastModified;

        public Entry(FileListener listener, Path path)
        {
            this.weakListener = new WeakReference<FileListener>(listener);
            this.path = path;
            this.lastModified = path.toFile().lastModified();
        }

        public void check()
        {
            long lm = path.toFile().lastModified();
            if (lastModified != lm) {
                FileListener listener = this.weakListener.get();
                if (listener != null) {
                    this.lastModified = lm;
                    listener.fileChanged(path);
                }
            }
        }
    }
}
