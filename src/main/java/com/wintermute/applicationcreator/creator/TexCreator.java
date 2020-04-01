package com.wintermute.applicationcreator.creator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wintermute.applicationcreator.adapter.TexConverter;
import com.wintermute.applicationcreator.collector.DataCollector;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Gets data converted to tex and fits it into template
 */
public class TexCreator
{
    private String pattern = "(\\t)|(%)";
    private JsonObject data;

    public TexCreator(String path)
    {
        try (Reader reader = Files.newBufferedReader(
            Paths.get(TexCreator.class.getClassLoader().getResource(path).toURI())))
        {
            data = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException | URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    private Map<String, Object> getTexContent()
    {
        DataCollector dataCollector = new DataCollector(data);
        TexConverter converter = new TexConverter(dataCollector.getData());
        return converter.getConvertedData();
    }

    @SneakyThrows
    public void createTexFile(File file)
    {
        Map<String, Object> texContent = getTexContent();
        File out = writeNewFile();
        FileWriter fw = new FileWriter(out);
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.equals("\\customsection{Per}{sonal characteristics}"))
                {
                    System.out.println();
                }
                String s = line.replaceAll(pattern, "");
                if (texContent.keySet().contains(s))
                {
                    if (texContent.get(s) instanceof String)
                    {
                        fw.write((String) texContent.get(s));
                    } else if (texContent.get(s) instanceof List)
                    {
                        for (String texLine : (List<String>) texContent.get(s))
                        {
                            fw.write(texLine);
                            fw.write("\n");
                        }
                    }
                } else
                {
                    fw.write(line);
                }
                fw.write("\n");
            }
            fw.close();
        }
    }

    File writeNewFile()
    {
        final String path = "src/main/resources/texTemplate/out.tex";
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
