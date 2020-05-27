package com.wintermute.applicationcreator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wintermute.applicationcreator.adapter.CoverLetterConverter;
import com.wintermute.applicationcreator.adapter.CvConverter;
import com.wintermute.applicationcreator.collector.DataCollector;
import com.wintermute.applicationcreator.creator.CoverLetterCreator;
import com.wintermute.applicationcreator.creator.CvCreator;
import com.wintermute.applicationcreator.wrapper.TexWrapper;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Creates an application including cover letter and curriculum vitae from json file.
 *
 * @author wintermute
 */
public class ApplicationCreator
{
    public static void main(String[] args) throws URISyntaxException, IOException
    {
        Reader reader = Files.newBufferedReader(
            Paths.get(ApplicationCreator.class.getClassLoader().getResource("data.json").toURI()));
        JsonObject data = JsonParser.parseReader(reader).getAsJsonObject();

        DataCollector collector = new DataCollector(data);
        Map<String, Object> collectedData = collector.getData();

        TexWrapper texWrapper = new TexWrapper();

        texWrapper.createTex(new CoverLetterCreator(texWrapper.convert(new CoverLetterConverter(collectedData))),
            "texTemplate/coverTemplate.tex");
        texWrapper.createTex(new CvCreator(texWrapper.convert(new CvConverter(collectedData))),
            "texTemplate/cvTemplate.tex");
    }
}
