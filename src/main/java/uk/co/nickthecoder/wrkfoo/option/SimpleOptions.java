package uk.co.nickthecoder.wrkfoo.option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import groovy.lang.Binding;
import uk.co.nickthecoder.jguifier.util.Util;
import uk.co.nickthecoder.wrkfoo.Tool;
import uk.co.nickthecoder.wrkfoo.util.OSHelper;

public class SimpleOptions implements Options
{
    private List<Option> list;

    private Map<String, List<Option>> rowMap;

    private Map<String, Option> nonRowMap;

    private GroovyScriptlet ifScriptlet;

    public SimpleOptions()
    {
        list = new ArrayList<>();
        rowMap = new HashMap<>();
        nonRowMap = new HashMap<>();
    }

    public void setIfScript(String ifScript)
    {
        if (Util.empty(ifScript)) {
            ifScriptlet = null;
        } else {
            ifScriptlet = new GroovyScriptlet(ifScript);
        }
    }

    public boolean isApplicable(Tool<?> tool)
    {
        return isApplicable(tool, null);
    }

    public boolean isApplicable(Tool<?> tool, Object row)
    {
        if (ifScriptlet == null) {
            return true;
        }

        try {
            Object result = runScript(ifScriptlet, tool, row);
            return result == Boolean.TRUE;

        } catch (Exception e) {
            // TODO handle the exception
            e.printStackTrace();
            return false;
        }
    }

    private Object runScript(GroovyScriptlet scriplet, Tool<?> tool, Object row)
    {
        Binding bindings = new Binding();
        bindings.setProperty("tool", tool);
        bindings.setProperty("task", tool.getTask());
        bindings.setProperty("row", row);

        bindings.setProperty("os", OSHelper.instance);

        return scriplet.run(bindings);
    }

    public void clear()
    {
        list.clear();
        rowMap.clear();
        nonRowMap.clear();
    }

    @Override
    public Option getOption(Tool<?> tool, String code, Object row)
    {
        Option result = getRowOption(tool, code, row);
        if (result == null) {
            return getNonRowOption(tool, code);
        } else {
            return result;
        }
    }

    @Override
    public Option getRowOption(Tool<?> tool, String code, Object row)
    {
        if (!isApplicable(tool, row)) {
            return null;
        }

        List<Option> options = rowMap.get(code);
        if (options == null) {
            return null;
        }
        for (Option option : options) {
            if (option.isApplicable(tool, row)) {
                return option;
            }
        }

        return null;
    }

    @Override
    public Option getNonRowOption(Tool<?> tool, String code)
    {
        if (!isApplicable(tool)) {
            return null;
        }

        return nonRowMap.get(code);
    }

    public void add(Option option)
    {
        list.add(option);

        if (option.isRow()) {
            addToSet(rowMap, option, option.getCode());
            for (String alias : option.getAliases()) {
                addToSet(rowMap, option, alias);
            }
        } else {
            nonRowMap.put(option.getCode(), option);
            for (String alias : option.getAliases()) {
                nonRowMap.put(alias, option);
            }
        }
    }

    public void remove(Option option)
    {
        list.remove(option);

        if (option.isRow()) {
            removeFromSet(rowMap, option, option.getCode());
            for (String alias : option.getAliases()) {
                removeFromSet(rowMap, option, alias);
            }
        } else {
        }
    }

    private void addToSet(Map<String, List<Option>> map, Option option, String code)
    {
        List<Option> options = map.get(code);
        if (options == null) {
            options = new ArrayList<>();
            map.put(code, options);
        }
        options.add(option);
    }

    private void removeFromSet(Map<String, List<Option>> map, Option option, String code)
    {
        List<Option> options = map.get(code);
        if (options == null) {
            return;
        }

        options.remove(option);

        if (options.size() == 0) {
            map.remove(code);
        }
    }

    @Override
    public Iterable<Option> allOptions()
    {
        return list;
    }

    @Override
    public Iterable<Option> applicableOptions(Tool<?> tool)
    {
        List<Option> results = new ArrayList<>();

        if (!isApplicable(tool)) {
            return results;
        }

        for (Option option : allOptions()) {
            if (!option.isRow()) {
                if (option.isApplicable(tool, null)) {
                    results.add(option);
                }
            }
        }
        return list;

    }

    @Override
    public Iterable<Option> applicableOptions(Tool<?> tool, Object row)
    {
        List<Option> results = new ArrayList<>();

        if (!isApplicable(tool, row)) {
            return results;
        }

        for (Option option : allOptions()) {
            if (option.isApplicable(tool, row)) {
                results.add(option);
            }
        }
        return list;
    }

}
