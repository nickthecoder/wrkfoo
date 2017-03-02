package uk.co.nickthecoder.wrkfoo.option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleOptions implements Options
{
    private List<Option> list;
    
    private Map<String, Option> rowMap;

    private Map<String, Option> nonRowMap;
    
    public SimpleOptions()
    {
        list = new ArrayList<Option>();
        rowMap = new HashMap<String,Option>();
        nonRowMap = new HashMap<String,Option>();
    }

    @Override
    public Option getDefaultRowOption()
    {
        return rowMap.get("");
    }

    @Override
    public Option getRowOption(String code)
    {
        return rowMap.get(code);
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
            rowMap.put(option.getCode(), option);
        } else {
            nonRowMap.put(option.getCode(), option);
        }
    }

    @Override
    public Iterator<Option> iterator()
    {
        return list.iterator();
    }

}
