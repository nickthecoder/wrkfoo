package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.GroovyOptions;
import uk.co.nickthecoder.wrkfoo.option.OptionsGroup;

public class Resources
{
    public static final Resources instance = new Resources();

    public String editor;
    
    private File settingsDirectory;

    private File optionsDirectory;

    private File settingsFile;

    private OptionsGroup globalOptions;

    private Resources()
    {
        globalOptions = new OptionsGroup();

        settingsDirectory = Util.createFile(new File(System.getProperty("user.home")), ".config", "wrkfoo");
        settingsDirectory.mkdirs();

        optionsDirectory = Util.createFile(settingsDirectory, "options");
        optionsDirectory.mkdirs();

        settingsFile = new File(settingsDirectory, "settings.json");
        readSettings();
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

                    editor = root.getString("editor", "gedit" );
                    
                    EasyJson.Node jglobals = root.getArray("globalOptions");

                    for (EasyJson.Node jele : jglobals) {
                        String name = jele.getAsString();
                        globalOptions.add(new GroovyOptions(new File(optionsDirectory, name + ".json")));
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

    public File getOptionsFile(String name)
    {
        return new File(optionsDirectory, name + ".json");
    }

    public GroovyOptions readOptions(String name)
    {
        try {
            GroovyOptions result = new GroovyOptions(getOptionsFile(name));
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
                GroovyOptions fo = new GroovyOptions(new File(optionsDirectory, name + ".json"));
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
}
