package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.Parameter;
import uk.co.nickthecoder.jguifier.ParameterException;
import uk.co.nickthecoder.jguifier.ValueParameter;

public class TabSetData
{
    public List<TabData> tabs;

    public String description = "";

    public int width = 1000;

    public int height = 600;

    private transient File tabSetFile;

    public TabSetData(MainWindow mainWindow)
    {
        tabs = new ArrayList<TabData>();
        for (CommandTab commandTab : mainWindow.tabbedPane) {
            Command<?> command = commandTab.getCommand();

            TabData tabData = new TabData(command);
            tabs.add(tabData);
        }
        width = mainWindow.getWidth();
        height = mainWindow.getHeight();
        description = mainWindow.description;
        tabSetFile = mainWindow.tabSetFile;
    }

    public static TabSetData load(File file)
    {
        Gson gson = new Gson();

        JsonReader reader;
        try {
            reader = new JsonReader(new FileReader(file));
            TabSetData tsd = gson.fromJson(reader, TabSetData.class);
            tsd.tabSetFile = file;
            return tsd;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(File file, MainWindow mainWindow)
        throws FileNotFoundException
    {
        mainWindow.tabSetFile = file;

        TabSetData tsd = new TabSetData(mainWindow);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(tsd);

        PrintWriter out = null;
        try {
            out = new PrintWriter(file);
            out.println(json);
        } finally {
            out.close();
        }
    }

    public void openMainWindow()
    {
        MainWindow mainWindow = createMainWindow();
        mainWindow.setBounds(0, 0, width, height);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }

    public MainWindow createMainWindow()
    {
        MainWindow mainWindow = new MainWindow();
        mainWindow.description = description;
        mainWindow.tabSetFile = tabSetFile;

        for (TabData tabData : tabs) {
            Command<?> command = tabData.createCommand();
            mainWindow.addTab(command);
        }

        return mainWindow;
    }

    public static class TabData
    {
        public String commandClass;
        public Map<String, String> parameters;

        public TabData(Command<?> command)
        {
            commandClass = command.getClass().getName();
            parameters = new HashMap<String, String>();
            for (Parameter parameter : command.getParameters().getChildren()) {
                if (parameter instanceof ValueParameter) {
                    ValueParameter<?> vp = (ValueParameter<?>) parameter;
                    parameters.put(vp.getName(), vp.getStringValue());
                }
            }
        }

        public Command<?> createCommand()
        {
            try {
                @SuppressWarnings("unchecked")
                Class<Command<?>> klass = (Class<Command<?>>) Class.forName(commandClass);
                Command<?> command = klass.newInstance();

                GroupParameter gp = command.getParameters();
                for (String key : parameters.keySet()) {
                    String value = parameters.get(key);
                    Parameter parameter = gp.findParameter(key);
                    if ((parameter != null) && (parameter instanceof ValueParameter)) {
                        ValueParameter<?> vp = (ValueParameter<?>) parameter;
                        try {
                            vp.setStringValue(value);
                        } catch (ParameterException e) {
                            // Do nothing
                        }
                    }
                }

                return command;

            } catch (Exception e) {
                return null;
            }
        }
    }
}
