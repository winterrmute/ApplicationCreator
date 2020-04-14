package com.wintermute.applicationcreator.adapter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Rule sets and help functions to inherit for tex document converters.
 *
 * @author wintermute
 */
public abstract class TexConverter
{
    Map<String, Object> data;
    private static final List<String> RESERVED_CHARS = Arrays.asList("#", "$", "%", "&", "{", "}", "_", "~", "^");

    /**
     * @param target illegal tex char to escape
     * @return string sanitized for tex standard.
     */
    String sanitize(String target)
    {
        for (String illegalChar : RESERVED_CHARS)
        {
            if (target.contains(illegalChar))
            {
                String legalChar = "\\" + illegalChar;
                target = target.replace(illegalChar, legalChar);
            }
        }
        return target;
    }

    void buildStatement(StringBuilder target, String... keys)
    {
        for (String key : keys)
        {
            target.append(key);
        }
    }

    /**
     * mandatory method of data converters.
     * @return converted and validated data for latex.
     */
    public abstract Map<String, Object> getConvertedData();
}
