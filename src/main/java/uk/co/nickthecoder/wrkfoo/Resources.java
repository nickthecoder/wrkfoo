package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.GroovyOptions;
import uk.co.nickthecoder.wrkfoo.option.OptionsGroup;
import uk.co.nickthecoder.wrkfoo.option.ReloadableOptions;

public class Resources
{
    public static final Resources instance = new Resources();

    static {
        Resources.instance.readSettings();
    }

    public String editor;

    private File homeDirectory;

    private File settingsDirectory;

    private File optionsDirectory;

    private File tabsDirectory;

    private File settingsFile;

    private OptionsGroup globalOptions;

    private Set<ReloadableOptions> reloadableOptions;

    private Resources()
    {
        reloadableOptions = new HashSet<ReloadableOptions>();

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

                    EasyJson.Node jglobals = root.getArray("globalOptions");

                    for (EasyJson.Node jele : jglobals) {
                        String name = jele.getAsString();
                        GroovyOptions options = new GroovyOptions();
                        options.load(new File(optionsDirectory, name + ".json"));
                        globalOptions.add(options);
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
        return new File(optionsDirectory, name + ".json");
    }

    public GroovyOptions readOptions(String name)
    {
        try {
            GroovyOptions result = new GroovyOptions();
            result.load(getOptionsFile(name));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public OptionsGroup createGroupOptions(String... optionsName)
    {
        OptionsGroup result = new OptionsGroup();

        for (String name : optionsName) {
            try {
                GroovyOptions fo = new GroovyOptions();
                fo.load(new File(optionsDirectory, name + ".json"));
                result.add(fo);
            } catch (IOException e) {
                e.printStackTrace();
                // Do nothing
            }
        }

        result.add(globalOptions);

        return result;
    }

    public static Icon icon(String name)
    {
        try {
            return new ImageIcon(ImageIO.read(Resources.class.getResource(name)));
        } catch (Exception e) {
            return null;
        }
    }

    public void RegisterReloadableOptions(ReloadableOptions ro)
    {
        reloadableOptions.add(ro);
    }

    public void reloadOptions()
    {
        for (ReloadableOptions ro : reloadableOptions) {
            ro.reload();
        }
    }
}
