package com.wintermute.applicationcreator.data;

import com.google.gson.JsonObject;
import com.wintermute.applicationcreator.model.Applicant;
import com.wintermute.applicationcreator.model.complex.CategoryGroup;
import com.wintermute.applicationcreator.model.Contact;
import com.wintermute.applicationcreator.model.CoverLetter;
import com.wintermute.applicationcreator.model.Language;
import com.wintermute.applicationcreator.model.complex.Career;
import com.wintermute.applicationcreator.model.complex.Project;
import com.wintermute.applicationcreator.model.complex.Skill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Provides parsed data in LATEX format.
 *
 * @author wintermute
 */
public class DocumentContentProvider
{
    private final DocumentContentParser contentParser;

    /**
     * @param data collected data about applicant, recipient and cover letter.
     */
    public Map<String, Function<String, String>> getDocumentContent(JsonObject data)
    {
        new DocumentContentSanitizer().sanitizeUserData(data);
        Applicant applicant = new DocumentContentFactory().getApplicant(data.getAsJsonObject("applicant"));

        Map<String, Function<String, String>> result = new HashMap<>();
        result.put("<header>", getCvHeader(applicant.getPersonalInfo()));
        result.put("<header_date>", getCoverLetterHeader(applicant.getPersonalInfo().getContact().getCity()));
        result.put("<applicant>", getApplicantBlock(applicant));
        result.put("<name>", createInlineEntry(applicant.getPersonalInfo().getFullName()));
        result.put("<applicantInfo>", getApplicantsInfo(applicant.getPersonalInfo().getContact()));
        result.put("<career>", createCareerEntries(applicant.getCareer()));
        result.put("<skills>", createSkillsEntries(applicant.getSkills()));
        result.put("<projects>", createProjectEntries(applicant.getProjects()));
        result.put("<hobbies>", createHobbyEntries(applicant.getHobbies()));
        result.put("<languages>", createLanguageEntries(applicant.getLanguages()));

        CoverLetter coverLetter = new DocumentContentFactory().getCoverLetter(data.getAsJsonObject("coverLetter"),
            applicant.getPersonalInfo());
        CoverLetter.Recipient recipient = coverLetter.getRecipient();
        result.put("<recipient>", getRecipientBlock(recipient));
        result.put("<applicationTopic>", createInlineEntry(coverLetter.getApplicationTopic()));
        result.put("<text>", createCoverLetterText(coverLetter.getParagraphs()));

        return result;
    }

    /**
     * Creates an instance.
     */
    public DocumentContentProvider()
    {
        contentParser = new DocumentContentParser();
    }

    /**
     * @param target information to replace the placeholder.
     * @return function replacing the placeholder with provided data.
     */
    private Function<String, String> createInlineEntry(String target)
    {
        return s -> s.replace(s, target);
    }

    /**
     * @param applicantsPersonalData personal data of applicant.
     * @return preconfigured header for curriculum vitae.
     */
    private Function<String, String> getCvHeader(Applicant.PersonalInfo applicantsPersonalData)
    {
        return s -> s.replace(s, contentParser.getParsedHeader(applicantsPersonalData));
    }

    /**
     * @param city of residence of applicant.
     * @return preconfigured date and place header for cover letter.
     */
    private Function<String, String> getCoverLetterHeader(String city)
    {
        return s -> s.replace(s, contentParser.getParsedCoverLetterHeader(city));
    }

    /**
     * @param target list of languages.
     * @return function replacing the placeholder with provided languages.
     */
    private Function<String, String> createLanguageEntries(List<Language> target)
    {
        return s -> s.replace(s, contentParser.getParsedLanguages(target));
    }

    /**
     * @param target list containing hobbies.
     * @return function to input list of hobbies in tex document.
     */
    private Function<String, String> createHobbyEntries(List<String> target)
    {
        return s -> s.replace(s, contentParser.getParsedHobbies(target));
    }

    /**
     * @param target list of projects grouped by category
     * @return function replacing the placeholder with prepared projects.
     */
    private Function<String, String> createProjectEntries(Map<CategoryGroup, List<Project>> target)
    {
        return createEntryForCategory(target, "Projects", false);
    }

    /**
     * @param target list of career grouped by category.
     * @return function replacing the placeholder with prepared projects.
     */
    private Function<String, String> createCareerEntries(Map<CategoryGroup, List<Career>> target)
    {
        return createEntryForCategory(target, "Career", true);
    }

    /**
     * @param target list of skills
     * @return function replacing the placeholder with prepared skills.
     */
    private Function<String, String> createSkillsEntries(Map<CategoryGroup, Map<CategoryGroup, List<Skill>>> target)
    {
        return s -> s.replace(s, contentParser.getParsedSkills(target));
    }

    /**
     * @param applicantsData contact information of applicant.
     * @return preconfigured contact for curriculum vitae
     */
    private Function<String, String> getApplicantsInfo(Contact applicantsData)
    {
        return createInlineEntry(contentParser.getParsedApplicantInfo(applicantsData));
    }

    /**
     * @param applicant contact data.
     * @return preconfigured block of recipient data.
     */
    private Function<String, String> getApplicantBlock(Applicant applicant)
    {
        return createInlineEntry(contentParser.getParsedApplicant(applicant.getPersonalInfo()));
    }

    /**
     * @param recipient contact data.
     * @return preconfigured block of recipient data.
     */
    private Function<String, String> getRecipientBlock(CoverLetter.Recipient recipient)
    {
        return createInlineEntry(contentParser.getParsedRecipient(recipient));
    }

    /**
     * @param paragraphs to create document body of it.
     * @return function providing preconfigured cover letter body text as tex document.
     */
    private Function<String, String> createCoverLetterText(List<String> paragraphs)
    {
        StringBuilder result = new StringBuilder();
        paragraphs.forEach(p -> result.append("\\coverparagraph{").append(p).append("}\n\n"));
        return s -> s.replace(s, result);
    }

    private <T> Function<String, String> createEntryForCategory(Map<CategoryGroup, List<T>> target, String section,
                                                                boolean isTable)
    {
        return s -> s.replace(s, contentParser.getParsedContentGroupedByCategory(target, section, isTable));
    }
}