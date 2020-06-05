package com.wintermute.applicationcreator.collector;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wintermute.applicationcreator.applicationData.Applicant;
import com.wintermute.applicationcreator.applicationData.Career;
import com.wintermute.applicationcreator.applicationData.Contact;
import com.wintermute.applicationcreator.applicationData.CoverLetter;
import com.wintermute.applicationcreator.applicationData.Recipient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Test the object mapping class.
 *
 * @author wintermute
 */
public class ObjectMapperTest
{
    private static ObjectMapper om;

    @BeforeAll
    public static void init() throws URISyntaxException, IOException
    {
        Reader reader = Files.newBufferedReader(
            Paths.get(DataCollectorTest.class.getClassLoader().getResource("data.json").toURI()));
        JsonObject data = JsonParser.parseReader(reader).getAsJsonObject();
        DataCollector dc = new DataCollector(data);
        om = new ObjectMapper(dc.getData());
    }

    @Test
    public void testApplicant()
    {
        Applicant applicant = om.getApplicant();
        assertThat(applicant.getJobTitle()).isEqualTo("Sicherheitsspezialist");

        Contact contact = applicant.getContact();
        assertThat(contact.getEmail()).isEqualTo("ajensen@sarifindustries.com");

        Map<String, List<Career>> career = applicant.getCareer();
        assertThat(career.size()).isEqualTo(2);

        List<Career> professionalCareer = career.get("professionalCareer");
        assertThat(professionalCareer.size()).isEqualTo(4);
        assertThat(professionalCareer.get(0).getJob()).isEqualTo("Sicherheitschef");

        List<String> hobbies = applicant.getHobbies();
        assertThat(hobbies.get(0)).isEqualTo("Hacken");
    }

    @Test
    public void getCoverLetter(){
        CoverLetter coverLetter = om.getCoverLetter();
        Recipient recipient = coverLetter.getRecipient();
        assertThat(recipient.getCompany()).isEqualTo("Sarif Industries");
        assertThat(recipient.getContact().getAddress()).isEqualTo("38 Sandy Ter, Hanson");
    }
}
