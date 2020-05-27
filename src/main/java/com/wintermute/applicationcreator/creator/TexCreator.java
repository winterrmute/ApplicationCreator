package com.wintermute.applicationcreator.creator;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Gets data converted to tex and fits it into template
 */
public abstract class TexCreator
{

    public abstract void create(File file);

    final Map<String, Object> data;

    public TexCreator(Map<String, Object> data)
    {
        this.data = data;
    }

    File writeNewFile(String name)
    {
        final String path = "src/main/resources/texTemplate/" + name + ".tex";
        File result = new File(path);
        try
        {
            if (result.createNewFile())
            {
                System.out.println("File created: " + result.getName());
            } else
            {
                System.out.println("File already exists.");
            }
        } catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return result;
    }
}
