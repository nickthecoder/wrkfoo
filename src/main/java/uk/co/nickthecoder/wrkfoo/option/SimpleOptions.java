package uk.co.nickthecoder.wrkfoo.option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleOptions implements Options
{
    private List<Option> list;

    private Map<String, List<Option>> rowMap;

    private Map<String, Option> nonRowMap;

    public SimpleOptions()
    {
        list = new ArrayList<>();
        rowMap = new HashMap<>();
        nonRowMap = new HashMap<>();
    }

    public void clear()
    {
        list.clear();
        rowMap.clear();
        nonRowMap.clear();
    }

    @Override
    public Option getOption(String code, Object row)
    {
        Option result = getRowOption(code, row);
        if (result == null) {
            return getNonRowOption(code);
        } else {
            return result;
        }
    }

    @Override
    public Option getRowOption(String code, Object row)
    {
        List<Option> options = rowMap.get(code);
        if (options == null) {
            return null;
        }
        for (Option option : options) {
            if (option.isApplicable(row)) {
                return option;
            }
        }
        
        return null;
    }

    @Override
    public Option getNonRowOption(String code)
    {
        return nonRowMap.get(code);
    }

    public void add(Option option)
    {
        list.add(option);
        
        if (option.isRow()) {
            addToSet(rowMap, option, option.getCode());
            for (String alias : option.getAliases()) {
                addToSet(rowMap, option, alias );
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
                removeFromSet(rowMap, option, alias );
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
    public Iterator<Option> iterator()
    {
        return list.iterator();
    }

}
