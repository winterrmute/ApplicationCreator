package com.wintermute.applicationcreator;

import com.google.gson.JsonParser;
import com.wintermute.applicationcreator.adapter.CvConverter;
import com.wintermute.applicationcreator.collector.DataCollector;
import com.wintermute.applicationcreator.collector.ObjectMapper;
import com.wintermute.applicationcreator.creator.CoverLetterCreator;
import com.wintermute.applicationcreator.creator.CvCreator;
import com.wintermute.applicationcreator.wrapper.TexWrapper;

import java.io.File;
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
        DataCollector collector = new DataCollector(JsonParser.parseReader(reader).getAsJsonObject());
        Map<String, Object> data = collector.getData();

        ObjectMapper objectMapper = new ObjectMapper(data);

        CoverLetterCreator coverLetterCreator = new CoverLetterCreator(objectMapper.getApplicant(), objectMapper.getCoverLetter());
        coverLetterCreator.create(new File(ApplicationCreator.class.getClassLoader().getResource("texTemplate/coverTemplate.tex").getFile()));

        //TODO: refactor cv creation
        TexWrapper texWrapper = new TexWrapper();
        texWrapper.createTex(new CvCreator(texWrapper.convert(new CvConverter(data))), "texTemplate/cvTemplate.tex");
    }
}
