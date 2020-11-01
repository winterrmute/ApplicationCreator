package com.wintermute.applicationcreator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wintermute.applicationcreator.ApplicationCreator;
import com.wintermute.applicationcreator.model.Applicant;
import com.wintermute.applicationcreator.model.complex.CategoryGroup;
import com.wintermute.applicationcreator.model.CoverLetter;
import com.wintermute.applicationcreator.model.complex.Career;
import com.wintermute.applicationcreator.model.complex.ElementWithTitle;
import com.wintermute.applicationcreator.model.complex.Project;
import com.wintermute.applicationcreator.model.complex.Skill;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Test read data from json and parsed result for LATEX format.
 *
 * @author wintermute
 */
public class DocumentContentTest
{
    static JsonObject userData;
    static DocumentContentParser parser;
    static Applicant applicant;

    @BeforeAll
    public static void collectData() throws Exception
    {
        parser = new DocumentContentParser();

        URL resource = ApplicationCreator.class.getClassLoader().getResource("data.json");

        if (resource != null)
        {
            Reader reader = Files.newBufferedReader(Paths.get(resource.toURI()));
            userData = JsonParser.parseReader(reader).getAsJsonObject();
            applicant = new DocumentContentFactory().getApplicant(userData.get("applicant").getAsJsonObject());
        } else
        {
            throw new Exception("Could not read file: data.json");
        }
    }

    @Test
    public void sanitizerTest(){
        JsonObject sanitized = userData.get("sanitizerTest").getAsJsonObject();
        new DocumentContentSanitizer().sanitizeUserData(sanitized);

        assertEquals(sanitized.get("easy").getAsString(), "\\#hello it\\$s m\\%e");

        String insideList = sanitized.getAsJsonArray("list").get(0).getAsString();
        assertEquals(insideList, "\\{sanitize me\\}");

        String nestedValue = sanitized.getAsJsonObject("findMe").get("inside").getAsString();
        assertEquals(nestedValue, "L\\_O\\_L \\^ found me");
    }

    @Test
    public void testParsingSkills() throws URISyntaxException, IOException
    {
        Map<CategoryGroup, Map<CategoryGroup, List<Skill>>> skills = applicant.getSkills();
        List<CategoryGroup> skillCategories = new ArrayList<>(skills.keySet());
        assertEquals(skillCategories.get(0).getTitle(), "Tactical");

        Map<CategoryGroup, List<Skill>> skillsGroups = skills.get(skillCategories.get(0));
        assertEquals(skillsGroups.size(), 3);

        List<CategoryGroup> groupsOfSkills = new ArrayList<>(skillsGroups.keySet());
        checkSortedSequence(groupsOfSkills, Map.of(0, "Tech", 1, "Strategy", 2, "Combat"));

        checkSortedSequence(skillsGroups.get(groupsOfSkills.get(1)),
            Map.of(0, "terrain knowledge", 1, "Reading architecture blueprints", 2, "Unit coordination"));

        String parsingResult = parser.getParsedSkills(skills);
        assertParsingResult("parsedSkills", parsingResult);
    }

    @Test
    public void testParsingCareer() throws URISyntaxException, IOException
    {
        Map<CategoryGroup, List<Career>> career = applicant.getCareer();
        assertEquals(career.size(), 2);

        ArrayList<CategoryGroup> careerCategories = new ArrayList<>(career.keySet());
        checkSortedSequence(careerCategories, Map.of(0, "education", 1, "professional career"));
        checkSortedSequence(career.get(careerCategories.get(1)),
            Map.of(0, "Sarif Industries", 1, "Detroit Police Department_3", 2, "Detroit Police Department_2", 3,
                "Detroit Police Department_1"));

        String parsingResult = parser.getParsedContentGroupedByCategory(career, "Career", true);
        assertParsingResult("parsedCareer", parsingResult);
    }

    @Test
    public void testParsingProjects() throws URISyntaxException, IOException
    {
        Map<CategoryGroup, List<Project>> projects = applicant.getProjects();
        assertEquals(projects.size(), 2);

        ArrayList<CategoryGroup> projectCategories = new ArrayList<>(projects.keySet());
        checkSortedSequence(projectCategories, Map.of(0, "professional", 1, "private"));
        List<Project> privateProjects = projects.get(projectCategories.get(1));
        checkSortedSequence(privateProjects, Map.of(0, "another Project", 1, "example private project 1"));

        Project project = privateProjects.get(0);
        assertEquals(project.getProgrammingLanguages().size(), 1);
        assertEquals(project.getProgrammingLanguages().get(0), "example languages");

        String parsingResult = parser.getParsedContentGroupedByCategory(projects, "Projects", false);
        assertParsingResult("parsedProjects", parsingResult);
    }

    @Test
    public void testParsingHobbies() throws URISyntaxException, IOException
    {
        List<String> hobbies = applicant.getHobbies();
        assertEquals(hobbies.size(), 4);
        assertParsingResult("parsedHobbies", parser.getParsedHobbies(hobbies));
    }

    @Test
    public void getCoverLetter()
    {
        CoverLetter result = new DocumentContentFactory().getCoverLetter(userData.getAsJsonObject("coverLetter"),
            applicant.getPersonalInfo());
        assertNotNull(result);
    }

    private void checkSortedSequence(List<? extends ElementWithTitle> target, Map<Integer, String> checks)
    {
        checks.forEach((key, value) -> assertEquals(target.get(key).getTitle(), value));
    }

    private String getExceptedParsedOutput(String fileName) throws IOException, URISyntaxException
    {
        return Files.readString(
            Path.of(DocumentContentTest.class.getClassLoader().getResource("parsedOutput/" + fileName).toURI()),
            StandardCharsets.UTF_8);
    }

    private void assertParsingResult(String fileNameWithExpectedResult, String parsingTarget)
        throws IOException, URISyntaxException
    {
        String expectedParsedSkills = getExceptedParsedOutput(fileNameWithExpectedResult);
        assertEquals(expectedParsedSkills, parsingTarget);
    }
}