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
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

import uk.co.nickthecoder.jguifier.ParameterException;
import uk.co.nickthecoder.jguifier.parameter.ValueParameter;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.util.HidingSplitPane;

public class Project
{
    public List<TabData> tabs;

    public String description = "";

    public int width = 1000;

    public int height = 600;

    private transient File projectFile;

    public Project(MainWindow mainWindow)
    {
        tabs = new ArrayList<>();
        for (Tab tab : mainWindow.mainTabs) {
            Tool<?> tool = tab.getMainHalfTab().getTool();
            HalfTab otherHalf = tab.getOtherHalfTab();
            Tool<?> otherTool = otherHalf == null ? null : otherHalf.getTool();

            TabData tabData = new TabData(tool, otherTool);
            tabs.add(tabData);
        }
        width = mainWindow.getWidth();
        height = mainWindow.getHeight();
        description = mainWindow.description;
        projectFile = mainWindow.projectFile;
    }

    public static Project load(File file)
    {
        Gson gson = new Gson();

        JsonReader reader;
        try {
            reader = new JsonReader(new FileReader(file));
            Project tsd = gson.fromJson(reader, Project.class);
            tsd.projectFile = file;
            return tsd;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(File file, MainWindow mainWindow)
        throws FileNotFoundException
    {
        mainWindow.projectFile = file;

        Project tsd = new Project(mainWindow);

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
        mainWindow.projectFile = projectFile;

        for (TabData tabData : tabs) {
            Tool<?> tool = tabData.createMainTool();
            if (tool == null) {
                continue;
            }

            Tool<?> otherTool = tabData.createOtherTool();
            
            mainWindow.addTab(tool, otherTool);
            String tt = tabData.titleTemplate == null ? "%t" : tabData.titleTemplate;
            Tab tab = tool.getHalfTab().getTab();
            tab.setTitleTemplate(tt);
            tab.setShortcut(tabData.shortcut);
            // We added the tab before giving it its titleTemplate, so now we need to update it.
            tab.getMainTabs().updateTitle(tab);
        }

        return mainWindow;
    }

    public static class TabData
    {
        public String titleTemplate = "%t";
        public String shortcut;

        @SerializedName(value = "creationString", alternate = { "toolClass" })
        public String creationString;
        public Map<String, String> parameters;
        boolean showParameters = false;
        @SerializedName("optionsName")
        public String overrideOptionsName;

        @SerializedName(value = "otherCreationString")
        public String otherCreationString;
        public Map<String, String> otherParameters;
        boolean otherShowParameters = false;
        @SerializedName("otherOoptionsName")
        public String otherOverrideOptionsName;

        public TabData(Tool<?> tool, Tool<?> otherTool)
        {
            titleTemplate = tool.getHalfTab().getTab().getTitleTemplate();
            shortcut = tool.getHalfTab().getTab().getShortcut();

            creationString = tool.getCreationString();
            parameters = new HashMap<>();
            for (ValueParameter<?> parameter : tool.getTask().valueParameters()) {
                parameters.put(parameter.getName(), parameter.getStringValue());
            }
            showParameters = tool.getToolPanel().getSplitPane().getState() != HidingSplitPane.State.LEFT;
            overrideOptionsName = tool.getOverrideOptionsName();

            if (otherTool != null) {
                otherCreationString = otherTool.getCreationString();
                otherParameters = new HashMap<>();
                for (ValueParameter<?> parameter : otherTool.getTask().valueParameters()) {
                    otherParameters.put(parameter.getName(), parameter.getStringValue());
                }
                otherShowParameters = otherTool.getToolPanel().getSplitPane().getState() != HidingSplitPane.State.LEFT;
                otherOverrideOptionsName = otherTool.getOverrideOptionsName();
            }
        }

        public Tool<?> createMainTool()
        {
            return createTool(creationString, parameters, showParameters, overrideOptionsName);
        }

        public Tool<?> createOtherTool()
        {
            if (Util.empty(otherCreationString)) {
                return null;
            }
            return createTool(otherCreationString, otherParameters, otherShowParameters, otherOverrideOptionsName);
        }

        @SuppressWarnings("unchecked")
        private static Tool<?> createTool(
            String creationString, Map<String, String> parameters,
            boolean showParameters, String overrideOptionsName)
        {
            try {
                Class<Tool<?>> klass;

                if (creationString.endsWith(".groovy")) {
                    klass = (Class<Tool<?>>) Resources.getInstance().loadGroovyClass(new File(creationString));

                } else {
                    klass = (Class<Tool<?>>) Class.forName(creationString);
                }
                Tool<?> tool = klass.newInstance();

                for (ValueParameter<?> parameter : tool.getTask().valueParameters()) {
                    if ((parameters.containsKey(parameter.getName()) && (parameter instanceof ValueParameter))) {

                        String value = parameters.get(parameter.getName());
                        try {
                            parameter.setStringValue(value);
                        } catch (ParameterException e) {
                            // Do nothing
                        }
                    }
                }

                tool.getToolPanel().getSplitPane()
                    .setState(showParameters ? HidingSplitPane.State.BOTH : HidingSplitPane.State.LEFT);

                tool.setOverrideOptionsName(overrideOptionsName);

                return tool;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
