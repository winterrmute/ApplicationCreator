package com.wintermute.applicationcreator.collector;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects application data and translates it to model.
 */
public class DataCollector
{
    private JsonObject applicationData;

    public DataCollector(JsonObject data)
    {
        applicationData = data;
    }

    Map<String, Object> getData()
    {
        Map<String, Object> result = new HashMap<>();
        for (String key : applicationData.keySet())
        {
            result.put(key, extractData(applicationData.get(key)));
        }
        return result;
    }

    private<T> T extractData(T data)
    {
        if (data instanceof JsonPrimitive)
        {
            return (T) data.toString().replace("\"", "");
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
}
