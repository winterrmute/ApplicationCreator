package com.wintermute.applicationcreator;

import com.google.gson.JsonParser;
import com.wintermute.applicationcreator.data.DataCollector;
import com.wintermute.applicationcreator.data.DocumentContentFactory;
import com.wintermute.applicationcreator.document.CoverLetterCreator;
import com.wintermute.applicationcreator.document.CvCreator;

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

        DocumentContentFactory objectMapper = new DocumentContentFactory(data);

        CoverLetterCreator coverLetterCreator =
            new CoverLetterCreator(objectMapper.getApplicant(), objectMapper.getCoverLetter());
        coverLetterCreator.createDocument(
            new File(ApplicationCreator.class.getClassLoader().getResource("texTemplate/coverTemplate.tex").getFile()));

        CvCreator cvCreator = new CvCreator(objectMapper.getApplicant());
        cvCreator.createDocument(
            new File(ApplicationCreator.class.getClassLoader().getResource("texTemplate/cvTemplate.tex").getFile()));
    }
}
