package uk.co.nickthecoder.wrkfoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.nickthecoder.jguifier.GroupParameter;
import uk.co.nickthecoder.jguifier.Parameter;
import uk.co.nickthecoder.jguifier.ValueParameter;

public class TabSetData
{
    public List<TabData> tabs;

    public TabSetData(MainWindow mainWindow)
    {
        tabs = new ArrayList<TabData>();
        for (CommandTab commandTab : mainWindow.tabbedPane) {
            Command<?> command = commandTab.getCommand();

            TabData tabData = new TabData(command);
            tabs.add(tabData);
        }
    }

    public MainWindow createMainWindow()
    {
        MainWindow mainWindow = new MainWindow();

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
                for ( String key : parameters.keySet() ) {
                    String value = parameters.get(key);
                    Parameter parameter = gp.findParameter(key);
                    if (( parameter != null) && (parameter instanceof ValueParameter) ) {
                        ValueParameter<?> vp = (ValueParameter<?>) parameter;
                        vp.setStringValue(value);
                    }
                }
                
                return command;
                
            } catch (Exception e) {
                return null;
            }
        }
    }
}
