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
import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

public class TabSetData
{
    public List<TabData> tabs;

    public String description = "";

    public int width = 1000;

    public int height = 600;

    private transient File tabSetFile;

    public TabSetData(MainWindow mainWindow)
    {
        tabs = new ArrayList<>();
        for (ToolTab toolTab : mainWindow.tabbedPane) {
            Tool tool = toolTab.getTool();

            TabData tabData = new TabData(tool);
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

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
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
            Tool tool = tabData.createTool();
            mainWindow.addTab(tool);
            String tt = tabData.titleTemplate == null ? "%t" : tabData.titleTemplate;
            ToolTab tab = tool.getToolTab();
            tab.setTitleTemplate(tt);
            tab.setShortcut(tabData.shortcut);
            // We added the tab before giving it its titleTemplate, so now we need to update it.
            tab.getTabbedPane().updateTabInfo(tab);
        }

        return mainWindow;
    }

    public static class TabData
    {
        public String toolClass;
        public Map<String, String> parameters;
        boolean showParameters = false;
        public String titleTemplate = "%t";
        public String shortcut;

        public TabData(Tool tool)
        {
            titleTemplate = tool.getToolTab().getTitleTemplate();
            shortcut = tool.getToolTab().getShortcut();

            toolClass = tool.getClass().getName();
            parameters = new HashMap<>();
            for (Parameter parameter : tool.getParameters().getChildren()) {
                if (parameter instanceof ValueParameter) {
                    ValueParameter<?> vp = (ValueParameter<?>) parameter;
                    parameters.put(vp.getName(), vp.getStringValue());
                }
            }
            showParameters = tool.getToolPanel().getSplitPane().getState() != HidingSplitPane.State.LEFT;
        }

        public Tool createTool()
        {
            try {
                @SuppressWarnings("unchecked")
                Class<Tool> klass = (Class<Tool>) Class.forName(toolClass);
                Tool tool = klass.newInstance();

                GroupParameter gp = tool.getParameters();
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

                tool.getToolPanel().getSplitPane()
                    .setState(showParameters ? HidingSplitPane.State.BOTH : HidingSplitPane.State.LEFT);

                return tool;

            } catch (Exception e) {
                return null;
            }
        }
    }
}
