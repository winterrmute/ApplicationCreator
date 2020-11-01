package com.wintermute.applicationcreator.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Iterates over every value coming from user data file and escapes reserved TEX characters.
 *
 * @author wintermute
 */
public class DocumentContentSanitizer
{
    private static final List<String> RESERVED_TEX_CHARS = Arrays.asList("#", "$", "%", "&", "{", "}", "_", "~", "^");

    /**
     * Iterate over provided data and touch every value containing reserved tex character and escape them.
     *
     * @param data user data.
     */
    void sanitizeUserData(JsonElement data)
    {
        if (data.isJsonArray())
        {
            JsonArray dataElem = data.getAsJsonArray();
            for (int i = 0; i < dataElem.size(); i++)
            {
                int[] pos = {i};
                Consumer<String> action = s -> dataElem.set(pos[0], new JsonPrimitive(escapeReservedCharactersInValue(s)));
                executeSanitizing(dataElem.get(i), action, dataElem.get(i));
            }
        } else if (data.isJsonObject())
        {
            ((JsonObject) data).entrySet().forEach(e ->
            {
                Consumer<String> action = s -> ((JsonObject) data).addProperty(e.getKey(), escapeReservedCharactersInValue(s));
                executeSanitizing(e.getValue(), action, e.getValue());
            });
        }
    }

    private void executeSanitizing(JsonElement scope, Consumer<String> sanitizing, JsonElement target)
    {
        if (scope.isJsonPrimitive())
        {
            try
            {
                sanitizing.accept(target.getAsString());
            } catch (IllegalStateException e)
            {
                //if the element can't be get as String it shouldn't probably be modified.
            }
        } else
        {
            sanitizeUserData(scope);
        }
    }

    private String escapeReservedCharactersInValue(String target)
    {
        List<String> toReplaceList = RESERVED_TEX_CHARS.stream().filter(target::contains).collect(Collectors.toList());
        for (String toReplaceChar : toReplaceList)
        {
            target = target.replace(toReplaceChar, "\\" + toReplaceChar);
        }
        return target;
    }
}
