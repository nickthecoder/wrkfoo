package uk.co.nickthecoder.wrkfoo.option;

import java.io.File;
import java.io.IOException;

import uk.co.nickthecoder.wrkfoo.EasyJson;
import uk.co.nickthecoder.wrkfoo.Resources;

public class GroovyOptions extends OptionsGroup implements ReloadableOptions
{
    private File file;

    private SimpleOptions simpleOptions;

    public GroovyOptions()
    {
        simpleOptions = new SimpleOptions();
        add(simpleOptions);
    }

    public void load(File file)
        throws IOException
    {
        load(file, true);
    }

    public void load(File file, boolean register)
    {
        this.file = file;
        loadOptions();
        if (register) {
            Resources.instance.RegisterReloadableOptions(this);
        }
    }

    private final void loadOptions()
    {
        EasyJson json = new EasyJson();
        try {
            EasyJson.Node root = json.open(file);

            String include = root.getString("include", null);
            if (include != null) {
                GroovyOptions included = new GroovyOptions();
                included.load(new File(file.getParent(), include), false);
                add(included);
            }

            EasyJson.Node jglobals = root.getArray("options");

            for (EasyJson.Node jopt : jglobals) {

                String code = jopt.getString("code");
                String label = jopt.getString("label");
                String ifScript = jopt.getString("if", null);
                String action = jopt.getString("groovy", null);
                if (action == null) {
                    action = jopt.getString( "action" );
                }
                boolean isRow = jopt.getBoolean("row", true);
                boolean isMulti = jopt.getBoolean("multi", false);

                GroovyOption option = new GroovyOption(code, label, action, ifScript, isRow, isMulti);

                simpleOptions.add(option);

                String aliases = jopt.getString("aliases", null);
                if (aliases != null) {
                    for (String alias : aliases.split(",")) {
                        simpleOptions.addAlias(option, alias);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            json.close();
        }
    }

    @Override
    public void reload()
    {
        if (file != null) {
            simpleOptions.clear();
            clear();
            add(simpleOptions);
            loadOptions();
        }
    }
}
