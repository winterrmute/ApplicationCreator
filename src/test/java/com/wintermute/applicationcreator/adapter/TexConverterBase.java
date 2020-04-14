package com.wintermute.applicationcreator.adapter;

import com.google.gson.JsonParser;
import com.wintermute.applicationcreator.collector.DataCollector;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Prepares tex formated data for tests.
 *
 * @author wintermute
 */
public class TexConverterBase
{
    static DataCollector dataCollector;

    @BeforeAll
    public static void initTexData() throws URISyntaxException, IOException
    {
        Reader reader =
            Files.newBufferedReader(Paths.get(CvConverterTest.class.getClassLoader().getResource("data.json").toURI()));
        dataCollector = new DataCollector(JsonParser.parseReader(reader).getAsJsonObject());
    }
}
