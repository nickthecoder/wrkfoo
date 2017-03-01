package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.io.IOException;

public class FileOptions extends SimpleOptions
{

    public FileOptions(File file)
        throws IOException
    {
        EasyJson json = new EasyJson();
        try {
            EasyJson.Node root = json.open(file);

            EasyJson.Node jglobals = root.getArray("options");

            for (EasyJson.Node jopt : jglobals) {

                String code = jopt.getString("code");
                String label = jopt.getString("label");
                String groovyScript = jopt.getString("groovy");
                boolean isRow = jopt.getBoolean("row", true);
                
                Option option = new GroovyOption(code, label, groovyScript, isRow);

                add(option);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            json.close();
        }
    }
}
