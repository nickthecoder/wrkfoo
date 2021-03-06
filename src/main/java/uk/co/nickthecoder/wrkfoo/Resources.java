package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.codehaus.groovy.control.CompilationFailedException;

import groovy.lang.GroovyClassLoader;
import uk.co.nickthecoder.jguifier.parameter.ChoiceParameter;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.option.Options;
import uk.co.nickthecoder.wrkfoo.option.OptionsData;
import uk.co.nickthecoder.wrkfoo.option.OptionsGroup;

public class Resources
{
    private static Resources instance;

    public static File settingsFile = Util.createFile(
        new File(System.getProperty("user.home")),
        ".config", "wrkfoo", "settings.json");

    public static Resources getInstance()
    {
        if (instance == null) {
            instance = new Resources();
            instance.readSettings();
            instance.globalOptions = instance.readOptions("global");
        }
        return instance;
    }

    /**
     * The name of your preferred text editor. Defaults to <code>gedit</code>
     */
    String editor = "gedit";

    List<URL> optionsPath = new ArrayList<>();;

    private File homeDirectory;

    private File settingsDirectory;

    private File projectsDirectory;

    private File customOptionsDirectory;

    private Options globalOptions;

    private Map<URL, OptionsData> optionsDataByURL;

    private List<File> groovyPath;

    private Resources()
    {
        homeDirectory = new File(System.getProperty("user.home"));

        settingsDirectory = Util.createFile(homeDirectory, ".config", "wrkfoo");
        customOptionsDirectory = new File(settingsDirectory, "optionData");

        optionsDataByURL = new HashMap<>();

        projectsDirectory = new File(settingsDirectory, "projects");
        projectsDirectory.mkdirs();

        groovyClassLoader = new GroovyClassLoader();
        groovyPath = new ArrayList<>();
    }

    public String getEditor()
    {
        return editor;
    }

    public Iterable<URL> optionsPath()
    {
        return optionsPath;
    }
    
    public File getSettingsDirectory()
    {
        return settingsDirectory;
    }

    public File getCustomOptionsDirectory()
    {
        return customOptionsDirectory;
    }

    public Options globalOptions()
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

            if (settings.optionsPath != null) {
                for (String urlString : settings.optionsPath) {
                    try {
                        this.optionsPath.add(new URL(urlString));
                    } catch (MalformedURLException e) {
                        System.err.println("Ignoring illegal optionData path  : " + urlString);
                    }
                }
            }
            if (settings.optionsPath.size() == 0) {
                // Add default optionData path
                File file = new File(settingsDirectory, "options");
                try {
                    optionsPath.add(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    // Really shouldn't get here unless the settings directory has been set incorrectly.
                    e.printStackTrace();
                }
                optionsPath.add(getClass().getResource("options/"));
            }

            // Add the user's bin directory if no groovy path was specified.
            if (settings.groovyPath == null) {
                settings.groovyPath = new ArrayList<>();
            }
            if (settings.groovyPath.size() == 0) {
                settings.groovyPath.add(new File(getHomeDirectory(), "bin").getPath());
            }

            // Add directories to the groovy class loader's classpath.
            groovyClassLoader = new GroovyClassLoader();
            groovyPath = new ArrayList<>();
            for (String directory : settings.groovyPath) {
                groovyPath.add(new File(directory));
                groovyClassLoader.addClasspath(directory);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Settings url : " + settingsFile + " not found. Using defaults.");
        }
    }

    public File getHomeDirectory()
    {
        return homeDirectory;
    }

    public File getProjectsDirectory()
    {
        return projectsDirectory;
    }

    public static Icon icon(String name)
    {
        try {
            return new ImageIcon(ImageIO.read(Resources.class.getResource(name)));
        } catch (Exception e) {
            return null;
        }
    }

    public OptionsData readOptionsData(URL url) throws MalformedURLException, IOException
    {
        OptionsData cached = optionsDataByURL.get(url);
        if (cached != null) {
            return cached;
        }

        OptionsData optionsData = OptionsData.load(url);
        optionsDataByURL.put(optionsData.url, optionsData);

        return optionsData;
    }

    public OptionsData readOptionsData(File directory, String name) throws URISyntaxException, IOException
    {
        return readOptionsData(directory.toURI().toURL(), name);
    }

    public OptionsData readOptionsData(URL parent, String name) throws URISyntaxException, IOException
    {
        URL url = new URL(parent, name + ".json");
        return readOptionsData(url);
    }

    public List<OptionsData> readOptionsData(String name)
    {
        List<OptionsData> result = new ArrayList<>();

        int count = 0;
        for (URL url : optionsPath) {
            try {
                result.add(readOptionsData(url, name));
                count++;
            } catch (URISyntaxException | IOException e) {
                // Do nothing
            }
        }
        if (count == 0) {
            System.err.println("Failed to find any optionData called " + name);
        }
        return result;
    }

    public Options readOptions(String name)
    {
        OptionsGroup result = new OptionsGroup();
        for (OptionsData optionsData : readOptionsData(name)) {
            result.add(optionsData.optionsGroup);
        }
        return result;
    }

    public ChoiceParameter<URL> createOptionsPathChoice(boolean includeAll)
    {
        ChoiceParameter<URL> result = new ChoiceParameter.Builder<URL>("optionsPath")
            .stretchy(true).parameter();

        if (includeAll) {
            result.setRequired(false);
            result.addChoice("<all>", null, "< all paths >");
        }

        for (URL url : Resources.getInstance().optionsPath()) {
            String label = url.toString();
            if (url.getProtocol().equals("file")) {
                try {
                    label = new File(url.toURI()).toString();
                } catch (URISyntaxException e) {
                }
            } else if (url.getProtocol().equals("jar")) {
                String s = url.toString();
                int pling = s.indexOf("!");
                if (pling > 0) {
                    label = "jar(" + s.substring(pling + 1) + ")";
                }
            }
            result.addChoice(label, url, label);
        }

        return result;
    }

    public ChoiceParameter<File> createOptionsDirectoryChoice()
    {
        ChoiceParameter<File> result = new ChoiceParameter.Builder<File>("optionsDirectory")
            .stretchy(true).parameter();

        for (URL url : Resources.getInstance().optionsPath()) {
            if (url.getProtocol().equals("file")) {
                try {
                    File file = new File(url.toURI());
                    result.addChoice(file);
                } catch (URISyntaxException e) {
                }
            }
        }
        return result;
    }

    public ChoiceParameter<File> createGroovyDirectoryChoice()
    {
        ChoiceParameter<File> result = new ChoiceParameter.Builder<File>("directory")
            .stretchy(true).parameter();

        for (File file : groovyPath) {
            result.addChoice(file);
        }

        return result;
    }

    /**
     * A cache of loaded groovy classes, so that they are not loaded multiple times.
     */
    private Map<File, Class<?>> groovyClasses = new HashMap<>();

    /**
     * All loaded groovy classes share the same classloader.
     */
    private GroovyClassLoader groovyClassLoader;

    public Class<?> loadGroovyClass(File script)
        throws CompilationFailedException, IOException
    {
        Class<?> klass = groovyClasses.get(script);
        if (klass != null) {
            return klass;
        }

        try {
            klass = groovyClassLoader.parseClass(script);
            groovyClasses.put(script, klass);
            return klass;

        } finally {
            try {
                groovyClassLoader.close();
            } catch (IOException e) {
            }
        }
    }

    public Tool<?> createGroovyTool(File file)
        throws CompilationFailedException, IOException, InstantiationException, IllegalAccessException
    {
        @SuppressWarnings("unchecked")
        Class<Tool<?>> klass = (Class<Tool<?>>) loadGroovyClass(file);
        Tool<?> tool = klass.newInstance();
        return tool;
    }

}
