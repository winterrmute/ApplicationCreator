package com.wintermute.applicationcreator.document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class describes the process of creating specified types of documents and required information.
 *
 * @author wintermute
 */
public class DocumentCreator
{
    /**
     * Takes template and generates document of it.
     *
     * @param template for specified type of document.
     */
    public void createDocument(File template, String fileName, Map<String, Function<String, String>> content)
    {
        File out = writeNewFile(fileName);
        Pattern pattern = Pattern.compile("(?><).*?([?=>]+)");
        try (BufferedReader br = new BufferedReader(new FileReader(template)); FileWriter fw = new FileWriter(out))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find())
                {
                    if (content.containsKey(matcher.group()))
                    {
                        String parsedUserData = content.get(matcher.group()).apply(matcher.group());
                        fw.write(line.replace(matcher.group(0), parsedUserData));
                    }
                } else
                {
                    fw.write(line + "\n");
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private File writeNewFile(String name)
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
                System.out.println("File already exists. Overwriting...");
            }
        } catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return result;
    }
}
