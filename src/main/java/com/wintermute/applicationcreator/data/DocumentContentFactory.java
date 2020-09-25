package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.model.Applicant;
import com.wintermute.applicationcreator.model.CoverLetter;
import com.wintermute.applicationcreator.model.Recipient;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Generates final document content provided by tags to replace in template file.
 *
 * @author wintermute
 */
public class DocumentContentFactory {

    /**
     * @param data collected data about applicant, recipient and cover letter.
     * @return
     */
    public Map<String, Function<String, String>> getDocumentContent(Map<String, Object> data) {
        DataOrganizer dataGetter = new DataOrganizer();
        Map<String, Object> applicantData = (Map<String, Object>) data.get("applicant");
        Applicant applicant = dataGetter.getApplicant(applicantData);

        DocumentContentProvider contentProvider = new DocumentContentProvider();
        Map<String, Function<String, String>> result = new HashMap<>();
        result.put("<header>", contentProvider.getCvHeader(applicant.getPersonalInfo()));
        result.put("<header_date>", contentProvider.getCoverLetterHeader(applicant.getContact().getCity()));
        result.put("<applicant>", contentProvider.getApplicantBlock(applicant));
        result.put("<name>", contentProvider.createInlineEntry(applicant.getPersonalInfo().getFullName()));
        result.put("<applicantInfo>", contentProvider.getApplicantsInfo(applicant.getContact()));
        result.put("<career>", contentProvider.createCareerEntries(applicant.getCareer()));
        result.put("<skills>", contentProvider.createSkillsEntries(applicant.getSkills()));
        result.put("<projects>", contentProvider.createProjectEntries(applicant.getProjects()));
        result.put("<hobbies>", contentProvider.createHobbyEntries(applicant.getHobbies()));
        result.put("<languages>", contentProvider.createLanguageEntries(applicant.getLanguages()));

        CoverLetter coverLetter = dataGetter.getCoverLetter((Map<String, Object>) data.get("coverLetter"), (Map<String, String>) data.get("recipient"));
        Recipient recipient = coverLetter.getRecipient();
        result.put("<recipient>", contentProvider.getRecipientBlock(recipient));
        result.put("<applicationTopic>", contentProvider.createInlineEntry(coverLetter.getApplicationTopic()));
        result.put("<text>", contentProvider.createCoverLetterText(coverLetter.getParagraphs()));

        return result;
    }
}