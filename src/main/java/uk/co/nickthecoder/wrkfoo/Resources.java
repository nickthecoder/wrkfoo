package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.GroovyOptions;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.option.OptionsGroup;

public class Resources
{
    public static final Resources instance = new Resources();

    static {
        Resources.instance.readSettings();
    }

    /**
     * The name of your preferred text editor. Defaults to <code>gedit</code>
     */
    public String editor;

    /**
     * The name of the program used as a file manager. For example <code>nautilus</code>. If you are running Gnome, then
     * you can also use <code>gnome-open</code>, and this will use gnome's default file manager (which is usually
     * nautilus).
     */
    public String fileManager;

    public List<String> globalOptionsNames;

    private File homeDirectory;

    private File settingsDirectory;

    private File optionsDirectory;

    private File tabsDirectory;

    private File settingsFile;

    private OptionsGroup globalOptions;

    private Map<File, OptionsData> optionsDataByFile;

    private Resources()
    {
        globalOptionsNames = new ArrayList<String>();

        optionsDataByFile = new HashMap<File, OptionsData>();

        homeDirectory = new File(System.getProperty("user.home"));
        globalOptions = new OptionsGroup();

        settingsDirectory = Util.createFile(homeDirectory, ".config", "wrkfoo");
        settingsDirectory.mkdirs();

        tabsDirectory = new File(settingsDirectory, "tabs");
        tabsDirectory.mkdirs();

        optionsDirectory = Util.createFile(settingsDirectory, "options");
        optionsDirectory.mkdirs();

        settingsFile = new File(settingsDirectory, "settings.json");
    }

    public OptionsGroup globalOptions()
    {
        return globalOptions;
    }

    public void readSettings()
    {
        try {
            SettingsData settings = SettingsData.load(settingsFile);
            if (settings.editor != null) {
                this.editor = settings.editor;
            }
            if (settings.fileManager != null) {
                this.editor = settings.fileManager;
            }
            if (settings.globalOptions != null) {
                this.globalOptions.clear();
                this.globalOptionsNames = new ArrayList<String>( settings.globalOptions );
                for (String name : this.globalOptionsNames) {
                    this.globalOptions.add( this.readOptions(name));
                }
            }
            
        } catch (FileNotFoundException e) {
            System.err.println( "Settings file : " + settingsFile + " not found. Using defaults." );
        }
    }

    public File getHomeDirectory()
    {
        return homeDirectory;
    }

    public File getTabsDirectory()
    {
        return tabsDirectory;
    }

    public File getOptionsDirectory()
    {
        return optionsDirectory;
    }

    public File getOptionsFile(String name)
    {
        return new File(optionsDirectory, name);
    }

    public static Icon icon(String name)
    {
        try {
            return new ImageIcon(ImageIO.read(Resources.class.getResource(name)));
        } catch (Exception e) {
            return null;
        }
    }

    public OptionsData readOptionsData(File file)
    {
        OptionsData cached = optionsDataByFile.get(file);
        if (cached != null) {
            // System.out.println( "Using cached version of : " + file + " (" + cached.options.size() +")" );
            return cached;
        }

        OptionsData optionsData = OptionsData.load(file);
        optionsDataByFile.put(optionsData.file, optionsData);

        // System.out.println( "Loaded options : " + file + " (" + optionsData.options.size() + ")" );

        return optionsData;
    }

    public GroovyOptions readOptions(String name)
    {
        return readOptions(getOptionsFile(name));
    }

    public GroovyOptions readOptions(File file)
    {
        return readOptionsData(file).groovyOptions;
    }

    public void reloadOptions()
    {
        for (OptionsData optionsData : optionsDataByFile.values()) {
            // System.out.println( "Reloading options : " + optionsData.file );
            optionsData.reload();
        }
    }
}
