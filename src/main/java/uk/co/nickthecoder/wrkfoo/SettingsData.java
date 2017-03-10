package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
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

    public List<String> globalOptions;

    public SettingsData()
    {
        editor = Resources.instance.editor;
        fileManager = Resources.instance.fileManager;
        globalOptions = Resources.instance.globalOptionsNames;
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
            out.close();
        }
    }
}
