package com.wintermute.applicationcreator.creator;

import com.wintermute.applicationcreator.adapter.CoverLetterConverter;
import com.wintermute.applicationcreator.collector.DataCollector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Creates application cover letter in latex from extracted data.
 *
 * @author wintermute
 */
public class CoverLetterCreator extends TexCreator
{

    public CoverLetterCreator(Map<String, Object> data)
    {
        super(data);
    }

    @Override
    public void create(File file)
    {
        File out = writeNewFile("coverLetter");
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            FileWriter fw = new FileWriter(out);
            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.contains(":city:"))
                {
                    line = line.replace(":city:", ((Map) data.get("info")).get("city").toString());
                }
                if (line.contains(":name:"))
                {
                    line = line.replace(":name:",
                        ((Map) data.get("info")).get("firstName").toString() + " " + ((Map) data.get("info"))
                            .get("lastName")
                            .toString());
                }
                if (line.contains(":address:"))
                {
                    line = line.replace(":address:",
                        ((Map) data.get("info")).get("address").toString() + ", " + ((Map) data.get("info"))
                            .get("city")
                            .toString());
                }
                if (line.contains(":phone:"))
                {
                    line = line.replace(":phone:", ((Map) data.get("info")).get("phone").toString());
                }
                if (line.contains(":email:"))
                {
                    line = line.replace(":email:", ((Map) data.get("info")).get("email").toString());
                }
                if (line.contains(":website:"))
                {
                    line = line.replace(":website:", ((Map) data.get("info")).get("website").toString());
                }
                if (line.contains(":company:"))
                {
                    line = line.replace(":company:", ((Map) data.get("recipient")).get("company").toString());

                    if (line.contains(":contact\\_person:"))
                    {
                        line = line.replace(":contact\\_person:",
                            ((Map) data.get("recipient")).get("contact_person").toString());
                    }
                    if (line.contains(":recipient\\_address:"))
                    {
                        line = line.replace(":recipient\\_address:",
                            ((Map) data.get("recipient")).get("address").toString());
                    }
                    if (line.contains(":recipient\\_city:"))
                    {
                        line = line.replace(":recipient\\_city:",
                            ((Map) data.get("recipient")).get("city").toString());
                    }
                }
                if (line.contains(":application\\_topic:"))
                {
                    line = line.replace(":application\\_topic:",
                        ((Map) data.get("cover_letter")).get("application_topic").toString());
                }
                if (line.contains(":text:"))
                {
                    line = line.replace(":text:", "");
                    fw.write(line);
                    List<String> text = (List) ((Map) data.get("cover_letter")).get("paragraphs");
                    for (String elem : text)
                    {
                        fw.write("\\coverparagraph{" + elem + "}\n\n");
                    }
                    continue;
                }
                fw.write(line);
                fw.write("\n");
            }
            fw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
