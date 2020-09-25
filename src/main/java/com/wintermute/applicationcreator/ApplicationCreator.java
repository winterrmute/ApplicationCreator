package com.wintermute.applicationcreator;

import com.google.gson.JsonParser;
import com.wintermute.applicationcreator.data.DataCollector;
import com.wintermute.applicationcreator.data.DocumentContentFactory;
import com.wintermute.applicationcreator.document.DocumentCreator;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;

/**
 * Creates an application from json file including cover letter and curriculum vitae.
 *
 * @author wintermute
 */
public class ApplicationCreator
{
    public static void main(String[] args) throws URISyntaxException, IOException
    {
        Reader reader = Files.newBufferedReader(
            Paths.get(ApplicationCreator.class.getClassLoader().getResource("data.json").toURI()));

        DataCollector collector = new DataCollector();
        DocumentContentFactory objectMapper =
            new DocumentContentFactory();

        Map<String, Function<String, String>> documentContent = objectMapper.getDocumentContent(collector.getDataFromJson(JsonParser.parseReader(reader).getAsJsonObject()));
        DocumentCreator documentCreator = new DocumentCreator();
        documentCreator.createDocument(
            new File(ApplicationCreator.class.getClassLoader().getResource("texTemplate/coverTemplate.tex").getFile()),
            "coverLetter", documentContent);
        documentCreator.createDocument(
            new File(ApplicationCreator.class.getClassLoader().getResource("texTemplate/cvTemplate.tex").getFile()),
            "cv", documentContent);
    }
}
