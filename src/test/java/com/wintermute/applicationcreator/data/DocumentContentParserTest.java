package com.wintermute.applicationcreator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wintermute.applicationcreator.model.Applicant;
import com.wintermute.applicationcreator.model.CoverLetter;
import com.wintermute.applicationcreator.model.Recipient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.text.html.parser.DocumentParser;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;

/**
 * Test the object mapping class.
 *
 * @author wintermute
 */
public class DocumentContentParserTest
{
    private static Map<String, Function<String, String>> documentContent;
    private static DocumentContentParser parser;
    private static DataOrganizer dataOrganizer;
    private static Map<String, Object> testData;

    @BeforeAll
    public static void init() throws URISyntaxException, IOException
    {
        Reader reader = Files.newBufferedReader(
            Paths.get(DocumentContentFactory.class.getClassLoader().getResource("data.json").toURI()));
        JsonObject data = JsonParser.parseReader(reader).getAsJsonObject();
        DataCollector dc = new DataCollector();
        DocumentContentFactory om = new DocumentContentFactory();

        testData = dc.getDataFromJson(data);
        documentContent = om.getDocumentContent(testData);
        parser = new DocumentContentParser();
        dataOrganizer = new DataOrganizer();
    }

    @Test
    public void applicantsTest()
    {
        String testHolder = "<applicant>";
        String result = documentContent.get(testHolder).apply(testHolder);
        String expected = "\\about{\\thinfont\\coverlist{\\faUser\\ \\coversender Adam Jensen}\n" +
                "{\\faMapMarker\\ \\small 6127 Evergreen Rd}\n" +
                "{\\small \\faPhone\\ (313) 271-918}\n" +
                "{\\small \\faAt\\ ajensen@sarifindustries.com}\n" +
                "{\\small \\faGithub\\https://sarifindustries.com/ajensen}\n";
        assertEquals(expected, result);
    }

    @Test
    public void recipientTest()
    {
        CoverLetter testObject = dataOrganizer.getCoverLetter((Map<String, Object>) testData.get("coverLetter"), (Map<String, String>) testData.get("recipient"));
        System.out.println(parsedCoverLetterHeader);
        //TODO: write a test after refactor
    }
}
