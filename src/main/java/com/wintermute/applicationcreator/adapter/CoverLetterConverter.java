package com.wintermute.applicationcreator.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts cover letter data into tex for provided template.
 *
 * @author wintermute
 */
public class CoverLetterConverter extends TexConverter
{
    public CoverLetterConverter(Map<String, Object> data)
    {
        this.data = data;
    }

    private Map<String, Object> result = new HashMap<>();
    private Map<String, Object> tmp;

    @Override
    public Map<String, Object> getConvertedData()
    {
        convertData("info", new String[]{"firstName", "lastName", "jobtitle", "contact"});
        convertData("recipient", new String[]{"company", "address", "city"});
        convertData("cover_letter", new String[]{"application_topic", "paragraphs"});
        return result;
    }



    private void convertData(String key, String[] subkeys)
    {
        tmp = new HashMap<>();
        result.put(key, copyEntries((Map<String, Object>) data.get(key), subkeys));
    }

    private Map<String, Object> copyEntries(Map<String, Object> origin, String[] keys)
    {
        return dataIterator(origin, keys);
    }

    private Map<String, Object> dataIterator(Map<String, Object> origin, String[] keys)
    {
        for (String key : keys)
        {
            if (origin.get(key) instanceof String)
            {
                tmp.put(key, sanitize(origin.get(key).toString()));
            } else if (origin.get(key) instanceof List) {
                List<String> list = new ArrayList<>();
                ((List) origin.get(key)).stream().forEach(e-> list.add(sanitize(e.toString())));
                tmp.put(key, list);
            } else if (origin.get(key) instanceof Map)
            {
                Map<String, Object> nestedObject = (Map<String, Object>) origin.get(key);
                String[] nestedKeys =
                    Arrays.copyOf(((Map) origin.get(key)).keySet().toArray(), origin.keySet().size(), String[].class);
                dataIterator(nestedObject, nestedKeys);
            }
        }
        return tmp;
    }
}
