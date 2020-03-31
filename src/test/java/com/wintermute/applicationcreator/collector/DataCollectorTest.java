package com.wintermute.applicationcreator.collector;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class DataCollectorTest
{
    private static JsonObject data;

    @BeforeAll
    public static void init() throws URISyntaxException, IOException
    {
        Reader reader = Files.newBufferedReader(
            Paths.get(DataCollectorTest.class.getClassLoader().getResource("data.json").toURI()));
        data = JsonParser.parseReader(reader).getAsJsonObject();
    }

    @Test
    public void getDataFromJson()
    {
        DataCollector underTest = new DataCollector(data);
        Map<String, Object> data = underTest.getData();
        Map<String, Object> info = (Map<String, Object>) data.get("info");
        assertThat(info.get("firstName")).isEqualTo("Azathoth");
        Map<String, Object> contact = (Map<String, Object>) info.get("contact");
        assertThat(contact.get("address")).isEqualTo("Somwhere in universe");
        Map<String, String> languges = (Map) info.get("spokenLanguages");
        assertThat(languges.keySet().size()).isEqualTo(4);
        assertThat(languges.get("R’lyehian")).isEqualTo("1 native speaker");
    }
}
