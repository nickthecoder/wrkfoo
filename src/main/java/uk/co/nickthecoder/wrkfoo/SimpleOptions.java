package uk.co.nickthecoder.wrkfoo;

import java.util.HashMap;
import java.util.Map;

public class SimpleOptions implements Options
{
    private Map<String, Option> map;
    
    public SimpleOptions()
    {
        map = new HashMap<String,Option>();
    }

    @Override
    public Option getDefault()
    {
        return map.get("");
    }

    @Override
    public Option get(String shortcut)
    {
        return map.get(shortcut);
    }

    @Override
    public void add(String shortcut, Option option)
    {
        map.put(shortcut, option);
    }

}
