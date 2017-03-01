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
    public Option get(String code)
    {
        Option result = getUnsafe( code );
        if (result == null) {
            return NullOption.instance;
        }
        return result;
    }

    @Override
    public Option getUnsafe(String shortcut)
    {
        return map.get(shortcut);
    }

    @Override
    public void add(Option option)
    {
        map.put(option.getCode(), option);
    }
    
    @Override
    public boolean contains( String code )
    {
        return get(code) != null;
    }

}
