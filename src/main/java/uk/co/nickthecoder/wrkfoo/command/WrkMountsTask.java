package uk.co.nickthecoder.wrkfoo.command;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jguifier.Task;
import uk.co.nickthecoder.wrkfoo.ListResults;
import uk.co.nickthecoder.wrkfoo.command.WrkMountsTask.MountPoint;
import uk.co.nickthecoder.wrkfoo.util.OSCommand;

public class WrkMountsTask extends Task implements ListResults<MountPoint>
{
    public List<MountPoint> results;

    @Override
    public void body()
    {
        results = new ArrayList<MountPoint>();

        Iterable<FileStore> stores = FileSystems.getDefault().getFileStores();
        for (FileStore store : stores) {
            MountPoint mp = new MountPoint(store);
            if (accept(mp)) {
                results.add(mp);
            }
        }
    }

    public boolean accept(MountPoint mp)
    {
        if (mp.size <= 0) {
            return false;
        }
        if (mp.store.name().equals("tmpfs")) {
            return false;
        }
        if (mp.store.name().equals("udev")) {
            return false;
        }
        if (mp.store.name().equals("rootfs")) {
            return false;
        }
        return true;
    }

    @Override
    public List<MountPoint> getResults()
    {
        return results;
    }

    public class MountPoint
    {
        public FileStore store;

        long size = 0;
        long used = 0;
        long available = 0;
        File file = null;

        public MountPoint(FileStore store)
        {
            this.store = store;
            // Try to get the size and free space of the mount, and if not, stick with defaults of zero.
            try {
                size = store.getTotalSpace();
                used = (store.getTotalSpace() - store.getUnallocatedSpace());
                available = store.getUsableSpace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Look for a private field called "file" or "root" in the FileStore. (Only works for Unix type systems).
            try {
                Class<?> klass = store.getClass();
                Field field = null;
                do {
                    try {
                        field = klass.getDeclaredField("file");
                    } catch (Exception e) {
                        // Do nothing
                    }
                    if (field != null)
                        break;
                    klass = klass.getSuperclass();
                } while (klass != null);

                // If we found the field, great, get the path.
                if (field != null) {
                    field.setAccessible(true);
                    file = ((Path) field.get(store)).toFile();
                }

            } catch (Exception e) {
                // Couldn't find the actual mount point.
                // e.printStackTrace();
            }

        }

        /**
         * Suitable for <code>row</code> to be passed to {@link OSCommand#command(String, Object...)}
         * 
         * @return The path
         */
        public String toString()
        {
            return file.getPath();
        }
    }
}
