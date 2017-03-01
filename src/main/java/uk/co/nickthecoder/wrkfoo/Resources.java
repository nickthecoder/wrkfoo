package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.io.IOException;

import uk.co.nickthecoder.jguifier.util.Util;

public class Resources
{
    public static final Resources instance = new Resources();

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
    
                    EasyJson.Node jglobals = root.getArray("globalOptions");
                        
                    for (EasyJson.Node jele : jglobals) {
                        String name = jele.getAsString();
                        globalOptions.add(new FileOptions(new File(optionsDirectory, name + ".json")));
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

    public FileOptions readOptions( String name )
    {
        try {
            FileOptions result = new FileOptions(new File( optionsDirectory, name + ".json"));
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
                FileOptions fo = new FileOptions(new File(optionsDirectory, name + ".json"));
                result.add(fo);
            } catch (IOException e) {
                e.printStackTrace();
                // Do nothing
            }
        }

        result.add(globalOptions);

        return result;
    }
}
