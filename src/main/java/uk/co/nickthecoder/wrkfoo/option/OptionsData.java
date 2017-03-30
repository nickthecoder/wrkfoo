package uk.co.nickthecoder.wrkfoo.option;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

import uk.co.nickthecoder.wrkfoo.Resources;

/**
 * Data-only class for loading/saving optionData
 */
public class OptionsData
{

    public static OptionsData load(URL url) throws MalformedURLException, IOException
    {
        Gson gson = new Gson();

        JsonReader reader;
        InputStreamReader isr = new InputStreamReader(url.openStream());
        reader = new JsonReader(isr);
        OptionsData optionsData = gson.fromJson(reader, OptionsData.class);
        optionsData.url = url;

        if (optionsData.include == null) {
            optionsData.include = new ArrayList<>();
        }
        if (optionsData.optionData == null) {
            optionsData.optionData = new ArrayList<>();
        }

        optionsData.optionsGroup = new OptionsGroup();
        optionsData.createOptions();

        return optionsData;
    }

    public transient URL url;

    /**
     * When loading a single .json file, we end up with a complex structure, with this being the top-level.
     * The first Options in optionsGroup will be a SimpleOptions, containing the options loaded directly from the .json
     * file.
     * The remaining items are the INCLUDES. These are currently handled badly, because there will end up being
     * repetition.
     */
    public transient OptionsGroup optionsGroup;

    public transient SimpleOptions options;
    
    public List<String> include = new ArrayList<>();

    @SerializedName("options")
    public List<OptionData> optionData = new ArrayList<>();

    public OptionsData(File directory, String name) throws MalformedURLException
    {
        this(new File(directory, name + ".json").toURI().toURL());
    }

    public OptionsData(URL url)
    {
        this.url = url;
        optionsGroup = new OptionsGroup();
    }

    private void createOptions()
    {
        if (this.optionData != null) {
            options = new SimpleOptions();
            for (OptionData optionData : optionData) {
                GroovyOption groovyOption = optionData.createOption();
                options.add(groovyOption);
            }
            optionsGroup.add(options);
        }

        if (this.include != null) {
            for (String include : this.include) {
                Options includedOptions;
                includedOptions = Resources.getInstance().readOptions(include);
                optionsGroup.add(includedOptions);
            }
        }

    }

    public SimpleOptions getOptions()
    {
        return options;
    }
    
    public void reload()
    {
        optionsGroup.clear();
        createOptions();
    }

    public void save()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        String json = gson.toJson(this);

        PrintWriter out = null;
        try {
            File file = new File(url.toURI());
            out = new PrintWriter(file);
            out.println(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            out.close();
        }

    }

    public static class OptionData
    {
        transient Option option;
        
        public String code;
        public String label;

        @SerializedName(value = "action", alternate = { "groovy" })
        public String action;

        @SerializedName("if")
        public String ifScript;

        public Boolean row;
        public boolean multi;
        public boolean newTab;
        public boolean refreshResults;
        public boolean prompt;

        public List<String> aliases;

        public OptionData()
        {
            aliases = new ArrayList<>();
        }
        
        public GroovyOption createOption()
        {
            GroovyOption option = new GroovyOption(code, label, action, ifScript, isRow(), multi, newTab,
                refreshResults, prompt);
            this.option = option;

            if (aliases != null) {
                for (String alias : aliases) {
                    option.addAlias(alias);
                }
            }

            return option;
        }

        public Option getOption()
        {
            return option;
        }
        
        public boolean isRow()
        {
            return row == Boolean.FALSE ? false : true;
        }

        @Override
        public String toString()
        {
            return code + ":" + label + ":" + action + ":" + ifScript + ":" + row + ":" + multi + ":" + newTab + ":"
                + refreshResults + ":" + prompt;
        }
    }
}
