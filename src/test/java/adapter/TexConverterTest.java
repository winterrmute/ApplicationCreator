package adapter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wintermute.applicationcreator.adapter.TexConverter;
import com.wintermute.applicationcreator.collector.DataCollector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class TexConverterTest
{
    private static JsonObject data;

    @BeforeAll
    public static void init() throws URISyntaxException, IOException
    {
        Reader reader = Files.newBufferedReader(
            Paths.get(TexConverterTest.class.getClassLoader().getResource("data.json").toURI()));
        data = JsonParser.parseReader(reader).getAsJsonObject();
    }

    @Test
    public void convertData()
    {
        DataCollector dc = new DataCollector(data);
        TexConverter underTest = new TexConverter(dc.getData());
        String header = underTest.generateHeader();
        String info = underTest.generatePersonalInfo();
        List<String> education = underTest.getCareerInfo("educationalCareer");
        List<String> professional = underTest.getCareerInfo("professionalCareer");
        Map<String, List<String>> skills = underTest.getSkills();
        List<String> projects = underTest.getProjects();
        List<String> languages = underTest.getSpokenLanguages();
        String interests = underTest.getHobbys();
        String strenghts = underTest.getPersonalStrenghts();
        System.out.println(header + "\n" + info);
    }
}
