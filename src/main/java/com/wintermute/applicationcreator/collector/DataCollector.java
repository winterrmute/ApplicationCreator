package com.wintermute.applicationcreator.collector;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects application data and translates it to model.
 */
public class DataCollector
{
    private final JsonObject data;
    private static final List<String> ILLEGAL_LETTERS = Arrays.asList("#", "$", "%", "&", "{", "}", "_", "~", "^");

    /**
     * Creates an instance
     *
     * @param data cv information as json
     */
    public DataCollector(JsonObject data)
    {
        this.data = data;
    }

    public Map<String, Object> getData()
    {
        Map<String, Object> result = new HashMap<>();
        for (String key : data.keySet())
        {
            result.put(sanitize(key), extractData(data.get(key)));
        }
        return result;
    }

    private <T> T extractData(T data)
    {
        if (data instanceof JsonPrimitive)
        {
            String result = data.toString().replace("\"", "");
            return (T) sanitize(result);
        } else if (data instanceof JsonArray)
        {
            List<Object> innerElements = new ArrayList<>();
            for (int i = 0; i < ((JsonArray) data).size(); i++)
            {
                innerElements.add(extractData(((JsonArray) data).get(i)));
            }
            return (T) innerElements;
        } else if (data instanceof JsonObject)
        {
            Map<String, Object> element = new HashMap<>();
            for (String subKey : ((JsonObject) data).keySet())
            {
                Object receivedData = extractData(((JsonObject) data).get(subKey));
                element.put(subKey, receivedData);
            }
            return (T) element;
        }
        return null;
    }

    private String sanitize(String target)
    {
        for (String toMask : ILLEGAL_LETTERS)
        {
            if (target.contains(toMask))
            {
                String maskedLetter = "\\" + toMask;
                target = target.replace(toMask, maskedLetter);
            }
        }
        return target;
    }
}
