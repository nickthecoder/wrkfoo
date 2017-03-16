package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class SettingsData
{
    public static SettingsData load(File file)
        throws FileNotFoundException
    {
        Gson gson = new Gson();
        JsonReader reader;

        reader = new JsonReader(new FileReader(file));
        SettingsData data = gson.fromJson(reader, SettingsData.class);

        return data;
    }

    public String editor;

    public String fileManager;
    
    public List<String> optionsPath = new ArrayList<>();

    public SettingsData()
    {
        editor = Resources.getInstance().editor;
        fileManager = Resources.getInstance().fileManager;
        optionsPath = new ArrayList<>();
        for ( URL url : Resources.getInstance().optionsPath ) {
            optionsPath.add( url.toString());
        }
    }

    public void save(File file) throws FileNotFoundException
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        String json = gson.toJson(this);

        PrintWriter out = null;
        try {
            out = new PrintWriter(file);
            out.println(json);
        } finally {
            if ( out != null) {
                out.close();
            }
        }
    }
}
