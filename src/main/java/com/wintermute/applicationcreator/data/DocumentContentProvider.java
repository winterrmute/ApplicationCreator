package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.model.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Provides parsed content to embed into tex template.
 *
 * @author wintermute
 */
public class DocumentContentProvider
{

    private final DocumentContentParser contentParser;

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
    public Function<String, String> createInlineEntry(String target)
    {
        return s -> s.replace(s, target);
    }

    /**
     * @param applicantsPersonalData personal data of applicant.
     * @return preconfigured header for curriculum vitae.
     */
    public Function<String, String> getCvHeader(PersonalInfo applicantsPersonalData){
        return s -> s.replace(s, contentParser.getParsedHeader(applicantsPersonalData));
    }

    /**
     * @param city of residence of applicant.
     * @return preconfigured date and place header for cover letter.
     */
    public Function<String, String> getCoverLetterHeader(String city) {
        return s -> s.replace(s, contentParser.getParsedCoverLetterHeader(city).toString());
    }

    /**
     * @param target list of languages.
     * @return function replacing the placeholder with provided languages.
     */
    public Function<String, String> createLanguageEntries(List<Language> target)
    {
        return s -> s.replace(s, contentParser.getParsedLanguages(target));
    }

    /**
     * @param target list containing hobbies.
     * @return function to input list of hobbies in tex document.
     */
    public Function<String, String> createHobbyEntries(List<String> target)
    {
        return s -> s.replace(s, contentParser.getParsedHobbies(target));
    }

    /**
     * @param target list of projects grouped by category
     * @return function replacing the placeholder with prepared projects.
     */
    public Function<String, String> createProjectEntries(Map<CategoryGroup, List<Project>> target)
    {
        return createEntryForCategory(target, "Projects", false);
    }

    /**
     * @param target list of career grouped by category.
     * @return function replacing the placeholder with prepared projects.
     */
    public Function<String, String> createCareerEntries(Map<CategoryGroup, List<Career>> target)
    {
        return createEntryForCategory(target, "Career", true);
    }

    /**
     * @param target list of skills
     * @return function replacing the placeholder with prepared skills.
     */
    public Function<String, String> createSkillsEntries(Map<CategoryGroup, List<Skill>> target)
    {
        return s -> s.replace(s, contentParser.getParsedSkills(target));
    }

    /**
     * @param applicantsData contact information of applicant.
     * @return preconfigured contact for curriculum vitae
     */
    public Function<String, String> getApplicantsInfo(Contact applicantsData){
        return createInlineEntry(contentParser.getParsedApplicantInfo(applicantsData));
    }

    /**
     * @param applicant contact data.
     * @return preconfigured block of recipient data.
     */
    public Function<String, String> getApplicantBlock(Applicant applicant){
        return createInlineEntry(contentParser.getParsedApplicant(applicant));
    }

    /**
     * @param recipient contact data.
     * @return preconfigured block of recipient data.
     */
    public Function<String, String> getRecipientBlock(Recipient recipient){
        return createInlineEntry(contentParser.getParsedRecipient(recipient));
    }

    /**
     * @param paragraphs to create document body of it.
     * @return function providing preconfigured cover letter body text as tex document.
     */
    public Function<String, String> createCoverLetterText(List<String> paragraphs) {
        StringBuilder result = new StringBuilder();
        paragraphs.forEach(p -> result.append(p).append("\n\n"));
        return s -> s.replace(s, result);
    }

    private <T> Function<String, String> createEntryForCategory(Map<CategoryGroup, List<T>> target, String section,
                                                                boolean isTable)
    {
        return s -> s.replace(s, contentParser.getParsedContentGroupedByCategory(target, section, isTable).toString());
    }
}
