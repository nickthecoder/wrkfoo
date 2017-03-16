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
        System.out.println("OptionsData.load " + url);
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
     * A group of optionData. The first will be the optionData of the given name.
     * This will be a OptionsGroup, with a SimpleOptions for each set of optionData found within the path.
     * the remainder will be included optionData.
     */
    public transient OptionsGroup optionsGroup;

    public List<String> include = new ArrayList<>();

    @SerializedName("options")
    public List<OptionData> optionData = new ArrayList<>();

    public OptionsData(File directory, String name) throws MalformedURLException
    {
        this( new File(directory, name + ".json").toURI().toURL() );
    }

    public OptionsData(URL url)
    {
        this.url = url;
        optionsGroup = new OptionsGroup();
    }

    private void createOptions()
    {
        if (this.optionData != null) {
            SimpleOptions simple = new SimpleOptions();
            for (OptionData optionData : optionData) {
                GroovyOption groovyOption = optionData.createOption();
                simple.add(groovyOption);
            }
            optionsGroup.add(simple);
        }

        if (this.include != null) {
            for (String include : this.include) {
                Options includedOptions;
                includedOptions = Resources.getInstance().readOptions(include);
                optionsGroup.add(includedOptions);
            }
        }

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
            // TODO Check if it IS a file:// url
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

        public GroovyOption createOption()
        {
            return new GroovyOption(code, label, action, ifScript, isRow(), multi, newTab, refreshResults);
        }

        public boolean isRow()
        {
            return row == Boolean.FALSE ? false : true;
        }

        @Override
        public String toString()
        {
            return code + ":" + label + ":" + action + ":" + ifScript + ":" + row + ":" + multi + ":" + newTab + ":"
                + refreshResults;
        }
    }
}
