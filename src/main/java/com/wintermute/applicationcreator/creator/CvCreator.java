package com.wintermute.applicationcreator.creator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Creates application curriculum vitae in latex from extracted data.
 *
 * @author wintermute
 */
public class CvCreator extends TexCreator
{

    public CvCreator(Map<String, Object> data)
    {
        super(data);
    }

    @Override
    public void create(File file)
    {
        File out = writeNewFile("cv");
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            FileWriter fw = new FileWriter(out);
            String line;
            while ((line = br.readLine()) != null)
            {
                String pattern = "(\\t)|(%)";
                String s = line.replaceAll(pattern, "");
                if (data.containsKey(s))
                {
                    if (data.get(s) instanceof String)
                    {
                        fw.write((String) data.get(s));
                    } else if (data.get(s) instanceof List)
                    {
                        for (String texLine : (List<String>) data.get(s))
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
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
