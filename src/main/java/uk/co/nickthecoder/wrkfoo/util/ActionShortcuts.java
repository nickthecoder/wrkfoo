package uk.co.nickthecoder.wrkfoo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.KeyStroke;

import uk.co.nickthecoder.jguifier.util.Util;

public final class ActionShortcuts
{
    public static final ActionShortcuts instance = new ActionShortcuts();

    /**
     * Maps an action name to a comma separated string of keyboard shortcuts.
     * The values are split, and then parsed by {@link KeyStroke#getKeyStroke(String)} to make
     * an item in {@link #map}.
     */
    private Map<String, String> customMap;

    transient private Map<String, Shortcuts> map;

    private ActionShortcuts()
    {
        map = new HashMap<>();
        customMap = new HashMap<>();

        // MainWindow
        put("runNonRowOption", "ENTER");
        put("runNonRowOptionInNewTab", "ctrl+ENTER");
        put("promptNonRowOption", "F2");
        put("promptNonRowOptionInNewTab", "ctrl+F2");

        put("home", "ctrl+HOME");
        put("newWindow", "ctrl+N");
        put("quit", "ctrl+Q");
        put("newTab", "ctrl T");
        put("duplicateTab", "");
        put("closeTab", "ctrl+W");
        put("workTabSets", "");
        put("saveTabSet", "");
        put("exportTable", "");

        put("back", "alt+LEFT");
        put("forward", "alt+RIGHT");
        put("run", "F5");
        put("stop", "ctrl+ESCAPE");
        put("showError", "ctrl+E");
        put("previousTab", "alt+PAGE_UP");
        put("nextTab", "alt+PAGE_DOWN");
        put("jumpToToolBar", "F10");
        put("jumpToResults", "F11");
        put("jumpToParameters", "F12");

        put("runNonRowOption", "ENTER");
        put("runNonRowOptionInNewTab", "ctrl+ENTER");
        put("promptNonRowOption", "F2");
        put("promptNonRowOptionInNewTab", "ctrl+F2");

        // TabbedPane
        put("tabProperties", "ctrl+P");
        put("tabClose", "");

        // TabbedPane
        put("tab.properties", "ctrl+P");
        put("tab.close", "ctrl+W");

        // TableToolPanel
        put("runOptions", "ENTER");
        put("runOptionsInNewTab", "ctrl ENTER");
        put("promptOptions", "F2");
        put("promptOptionsInNewTab", "ctrl F2");

        // ToolPanel
        put("toggleLeftPane", "F9");
        put("toggleRightPane", "ctrl+F9");
        put("toolpanel.go", "ENTER");
        put("toolpanel.stop", "ctrl+ESCAPE");

        // ToolTab
        put("undoTool", "alt LEFT");
        put("redoTool", "alt RIGHT");

        // WrkFBase
        put("wrkf.upDirectory", "alt UP");

        // Editor
        put("documentOpen", "ctrl+O");

        // EditorPanel
        put("documentSave", "ctrl+S");
        put("documentRevert", "F5");
        put("editUndo", "ctrl+Z");
        put("editRedo", "ctrl+shift+Z");
        put("editCopy", "ctrl+C");
        put("editPaste", "ctrl+V");
        put("editFind", "ctrl+F");
        put("editReplace", "ctrl+H");
        put("editGoToLine", "ctrl+L");
        put("escape", "ESCAPE");

        // FindToolBar
        put("find.findPrev", "ctrl+shift+G");
        put("find.findNext", "ctrl+G");
        put("find.matchCase", "");
        put("find.matchWholeWord", "");
        put("find.matchRegex", "");

        // ReplaceDialog
        put("replace.closeDialog", "ESCAPE");
        put("replace.replaceAll", "");
        put("replace.replace", "");
        put("replace.replaceFind", "");
        put("replace.find", "");

        // Teminal
        put( "SimpleTerminalWidget.go", "ENTER" );
        put( "SimpleTerminalWidget.terminate", "ctrl+D" );
        
    }

    /**
     * Replaces the standard mapping with a different set of keystrokes.
     * 
     * @param actionName
     * @param strokes
     */
    public void custom(String actionName, String strokes)
    {
        customMap.put(actionName, strokes);
        put(actionName, strokes);
    }

    private void put(String actionName, String strokes)
    {
        Shortcuts shortcuts = new Shortcuts(strokes);
        map.put(actionName, shortcuts);
    }

    public Shortcuts get(String actionName)
    {
        return map.get(actionName);
    }

    public String tooltipSuffix(String actionName)
    {
        Shortcuts shortcuts = get(actionName);
        return shortcuts == null ? "" : shortcuts.tooltipSuffix;
    }

    public static class Shortcuts
    {
        public final List<KeyStroke> keyStrokes;

        public final String tooltipSuffix;

        public Shortcuts(String strokes)
        {
            keyStrokes = new ArrayList<>();
            String suffix = "";

            String[] array = strokes.split(",");

            boolean first = true;

            for (String stroke : array) {
                if (!Util.empty(stroke)) {
                    KeyStroke keyStroke = KeyStroke.getKeyStroke(stroke.replace('+', ' '));
                    keyStrokes.add(keyStroke);

                    if (first) {
                        suffix = " (" + stroke + ")";
                        first = false;
                    }
                }
            }
            tooltipSuffix = suffix;
        }
    }
}
