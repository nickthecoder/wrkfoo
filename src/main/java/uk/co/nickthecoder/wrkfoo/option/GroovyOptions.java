package uk.co.nickthecoder.wrkfoo.option;

import java.io.File;
import java.io.IOException;

import uk.co.nickthecoder.wrkfoo.EasyJson;

public class GroovyOptions extends SimpleOptions
{

    public GroovyOptions(File file)
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
                boolean isMulti = jopt.getBoolean("multi", false);
                
                GroovyOption option = new GroovyOption(code, label, groovyScript, isRow, isMulti);
                
                add(option);

                String aliases = jopt.getString( "aliases", null );
                if (aliases != null) {
                    for (String alias : aliases.split(",")) {
                        addAlias( option, alias );
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            json.close();
        }
    }
}
