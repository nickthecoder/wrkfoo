package uk.co.nickthecoder.wrkfoo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class EasyJson
{
    FileInputStream fis;

    public EasyJson()
    {
    }

    public Node open(File file) throws FileNotFoundException
    {
        fis = new FileInputStream(file);
        JsonParser parser = new JsonParser();
        JsonElement jroot = parser.parse(new InputStreamReader(fis));

        return new Node(jroot, file.getPath());
    }

    public void close()
    {
        try {
            fis.close();
        } catch (Exception e) {
            // Do nothing
        }
    }

    private static RuntimeException error(String e)
    {
        return new JsonException(e);
    }

    public class Node implements Iterable<Node>
    {
        private JsonElement ele;
        private String name;
        private JsonArray array;

        public Node(JsonElement ele, String name)
        {
            if (ele.isJsonArray()) {
                array = ele.getAsJsonArray();
            }
            this.ele = ele;
            this.name = name;
        }

        private JsonElement get(String name)
        {
            try {
                return ele.getAsJsonObject().get(name);
            } catch (Exception e) {
                throw error(this.name + " is not an object");
            }
        }
        
        public String getAsString()
        {
            try {
                return ele.getAsString();
            } catch (Exception e) {
                throw error ( name + " is not a String" );
            }
        }

        public String getString(String name)
        {
            try {
                return get(name).getAsString();
            } catch (Exception e) {
                throw error("Missing string attribute '" + name + "' in '" + this.name + "'");
            }
        }

        public String getString(String name, String defaultValue)
        {
            try {
                return get(name).getAsString();
            } catch (Exception e) {
                return defaultValue;
            }
        }

        public boolean getBoolean(String name)
        {
            try {
                return get(name).getAsBoolean();
            } catch (Exception e) {
                throw error("Missing boolean attribute '" + name + "' in '" + this.name + "'");
            }
        }

        public boolean getBoolean(String name, Boolean defaultValue)
        {
            try {
                return get(name).getAsBoolean();
            } catch (Exception e) {
                return defaultValue;
            }
        }

        public Node getArray(String name)
        {
            try {
                JsonArray array = get(name).getAsJsonArray();
                return new Node(array, name);
            } catch (Exception e) {
                throw error("No array named " + name + " in " + this.name);
            }

        }

        @Override
        public Iterator<Node> iterator()
        {
            if ( array == null ) {
                throw error( name + " is not an array" );
            }
            
            return new NodeIterator(array.iterator());
        }

        public class NodeIterator implements Iterator<Node>
        {
            private Iterator<JsonElement> wrapped;
            private int i = 0;

            public NodeIterator(Iterator<JsonElement> i)
            {
                wrapped = i;
            }

            @Override
            public boolean hasNext()
            {
                return wrapped.hasNext();
            }

            @Override
            public Node next()
            {
                return new Node(wrapped.next(), Node.this.name + "[" + i++ + "]");
            }

            @Override
            public void remove()
            {
                wrapped.remove();
            }

        }

    }

    public static class JsonException extends RuntimeException
    {
        public JsonException(String message)
        {
            super(message);
        }

    }
}
