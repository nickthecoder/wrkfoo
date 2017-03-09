package uk.co.nickthecoder.wrkfoo.option;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.wrkfoo.Resources;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

/**
 * Data-only class for loading/saving options
 */
public class OptionsData
{

    public static OptionsData load(File file)
    {
        Gson gson = new Gson();

        JsonReader reader;
        try {
            System.out.println( "Loading options " + file );
            reader = new JsonReader(new FileReader(file));
            OptionsData optionsData = gson.fromJson(reader, OptionsData.class);
            optionsData.file = file;

            optionsData.groovyOptions = new GroovyOptions();
            optionsData.createOptions();
            System.out.println( "Created options" );

            return optionsData;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public transient File file;

    public transient GroovyOptions groovyOptions;

    public List<String> include = new ArrayList<String>();

    public List<OptionData> options = new ArrayList<OptionData>();

    private OptionsData()
    {
    }

    private void createOptions()
    {
        System.out.println( "Creating options " + include.size() + " includes. " + options.size() + " options ");
        for (String include : include) {
            Options includedOptions = Resources.instance.readOptions(include);
            groovyOptions.add(includedOptions);
        }

        for (OptionData optionData : options) {
            System.out.println( "Creating option : "+ optionData );
            GroovyOption groovyOption = optionData.createOption();
            groovyOptions.add(groovyOption);
        }

    }

    public void reload()
    {
        groovyOptions.clear();
        createOptions();
    }

    public class OptionData
    {
        public String code;
        public String label;
        
        @SerializedName(value="action", alternate={"groovy"})
        public String action;
        
        @SerializedName("if")
        public String ifScript;
        
        private Boolean row;
        public boolean multi;

        public GroovyOption createOption()
        {
            System.out.println( "Creating option : " + code );

            return new GroovyOption(code, label, action, ifScript, isRow(), multi);
        }
        
        public boolean isRow()
        {
            return row == Boolean.FALSE ? false : true;
        }
        
        public String toString()
        {
            return code + ":" + label + ":" + action + ":" + ifScript + ":" + row + ":" + multi;
        }
    }
}
