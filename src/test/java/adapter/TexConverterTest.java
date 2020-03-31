package adapter;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(header).isEqualTo("\\headerbox{1.2cm}{darkgray}{white}{Azathoth\\\\ Outer}{pics/pic.jpg}");

        String info = underTest.generatePersonalInfo();
        assertThat(info).isEqualTo("\\faEnvelopeO\\/ Somwhere in universe |\\faMapMarker\\/ arkham |\\faPhone\\/ prayer |\\faAt\\protect\\/ prayer@god.org ");

        List<String> education = underTest.getCareerInfo("educationalCareer");
        assertThat(education.get(0)).isEqualTo("\\columntitle{08/1204 -- 06/1209} & \\activity{University of universe"
            + " chaos study}{Bachelor of Science}\\\\");

        List<String> professional = underTest.getCareerInfo("professionalCareer");
        assertThat(professional.get(0)).isEqualTo(
            "\\columntitle{15.05.1209 -- today} & \\activity{Unknown}{protagonist in " + "necronomicon}{god}\\\\");

        Map<String, List<String>> skills = underTest.getSkills();
        assertThat(skills.keySet().size()).isEqualTo(2);
        assertThat(skills.containsKey("active")).isEqualTo(true);
        assertThat(skills.get("active").get(0)).isEqualTo(
            "\\columntitle{chaos} & \\newlinelist{reality warping}{reality warping}");

        List<String> projects = underTest.getProjects();
        assertThat(projects.size()).isEqualTo(2);
        assertThat(projects.get(0)).startsWith(
            "\\columntitle{09.1215 -- 12.1220} & \\activity{warping reality}{Outer god}{distortion of space}\\");

        List<String> languages = underTest.getSpokenLanguages();
        assertThat(languages.size()).isEqualTo(4);
        assertThat(languages.get(0)).isEqualTo("\\columntitle{R’lyehian} & \\singleitem{native speaker}\\\\");

        String interests = underTest.getHobbys();
        assertThat(interests).isEqualTo("\\commaseparatedlist{Destroying worlds}{Beeing god}");

        String strenghts = underTest.getPersonalStrenghts();
        assertThat(strenghts).isEqualTo("\\commaseparatedlist{being god}{being blind}{motivated to do really bad things}");
    }
}
