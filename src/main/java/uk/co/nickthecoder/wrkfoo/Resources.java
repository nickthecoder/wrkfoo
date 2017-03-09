package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.util.HashMap;
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

    public String editor;

    public String fileManager;

    private File homeDirectory;

    private File settingsDirectory;

    private File optionsDirectory;

    private File tabsDirectory;

    private File settingsFile;

    private OptionsGroup globalOptions;

    private Map<File, OptionsData> optionsDataByFile;

    private Resources()
    {
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

    private void readSettings()
    {
        if (settingsFile.exists()) {

            try {
                EasyJson json = new EasyJson();
                try {
                    EasyJson.Node root = json.open(settingsFile);

                    editor = root.getString("editor", "gedit");
                    fileManager = root.getString("fileManager", "nautilus");

                    EasyJson.Node jglobals = root.getArray("globalOptions");

                    for (EasyJson.Node jele : jglobals) {
                        String name = jele.getAsString();
                        globalOptions.add(readOptions(name));
                    }
                } finally {
                    json.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            // Use default values
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

   
    
    public GroovyOptions readOptions(String name)
    {
        OptionsData cached = optionsDataByFile.get(getOptionsFile(name));
        if (cached != null) {
            System.out.println("Reusing cached options " + name);
            return cached.groovyOptions;
        }

        OptionsData optionsData = OptionsData.load(getOptionsFile(name));
        GroovyOptions result = optionsData.groovyOptions;
        optionsDataByFile.put(optionsData.file, optionsData);

        optionsDataByFile.put(optionsData.file, optionsData);
        return result;
    }

    public void reloadOptions()
    {
        for (OptionsData optionsData : optionsDataByFile.values()) {
            optionsData.reload();
        }
    }
}
