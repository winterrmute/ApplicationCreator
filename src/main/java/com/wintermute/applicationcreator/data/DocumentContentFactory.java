package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.data.mapper.CareerMapper;
import com.wintermute.applicationcreator.data.mapper.ProjectMapper;
import com.wintermute.applicationcreator.data.mapper.SkillsMapper;
import com.wintermute.applicationcreator.model.Applicant;
import com.wintermute.applicationcreator.model.Contact;
import com.wintermute.applicationcreator.model.CoverLetter;
import com.wintermute.applicationcreator.model.Language;
import com.wintermute.applicationcreator.model.PersonalInfo;
import com.wintermute.applicationcreator.model.Recipient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Generates final document content provided by tags to replace in template file.
 *
 * @author wintermute
 */
public class DocumentContentFactory
{
    private final Map<String, Object> data;
    private final Map<String, Function<String, String>> generatedDocumentContent;

    public DocumentContentFactory(Map<String, Object> data)
    {
        this.data = data;
        //TODO: notify the user when data is empty.
        generatedDocumentContent = new HashMap<>();
    }

    public Map<String, Function<String, String>> getDocumentContent()
    {
        DocumentContentProvider contentProvider = new DocumentContentProvider();

        Applicant applicant = getApplicant();

        generatedDocumentContent.put("<header>", contentProvider.createInlineEntry(
            applicant.getPersonalInfo().getFirstName() + "\\\\" + applicant.getPersonalInfo().getLastName() + "\\\\"
                + applicant.getPersonalInfo().getJobTitle() + "}{pics/pic.jpg}"));
        generatedDocumentContent.put("<header_date>", contentProvider.createInlineEntry(
            applicant.getContact().getCity() + ", den " + LocalDate
                .now()
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))));
        generatedDocumentContent.put("<name>",
            contentProvider.createInlineEntry(applicant.getPersonalInfo().getFullName()));
        generatedDocumentContent.put("address", contentProvider.createInlineEntry(applicant.getContact().getAddress()));
        generatedDocumentContent.put("<website>",
            contentProvider.createInlineEntry(applicant.getContact().getWebsite()));
        generatedDocumentContent.put("<street>",
            contentProvider.createInlineEntry(applicant.getContact().getAddress()));
        generatedDocumentContent.put("<city>",
            contentProvider.createInlineEntry(applicant.getContact().getCityWithZipcode()));
        generatedDocumentContent.put("<phonenumber>",
            contentProvider.createInlineEntry(applicant.getContact().getPhoneNumber()));
        generatedDocumentContent.put("<email>",
            contentProvider.createInlineEntry(applicant.getContact().getPhoneNumber()));
        generatedDocumentContent.put("<career>", contentProvider.createCareerEntries(applicant.getCareer()));
        generatedDocumentContent.put("<skills>",
            contentProvider.createSkillsEntries(applicant.getSkills())); //TODO: handle skills
        generatedDocumentContent.put("<projects>", contentProvider.createProjectEntries(applicant.getProjects()));
        generatedDocumentContent.put("<hobbies>", contentProvider.createInlineEntry("")); //TODO: handle projects
        generatedDocumentContent.put("<languages>",
            contentProvider.createLanguageEntries(applicant.getLanguages())); //TODO: handle projects

        CoverLetter coverLetter = getCoverLetter();
        Recipient recipient = coverLetter.getRecipient();
        generatedDocumentContent.put("<company>", contentProvider.createInlineEntry(recipient.getCompany()));
        generatedDocumentContent.put("<contactPerson>",
            contentProvider.createInlineEntry(recipient.getContactPerson()));
        generatedDocumentContent.put("<recipientAddress>",
            contentProvider.createInlineEntry(recipient.getContact().getAddress()));
        generatedDocumentContent.put("<recipientCity>",
            contentProvider.createInlineEntry(recipient.getContact().getCityWithZipcode()));
        generatedDocumentContent.put("<applicationTopic>",
            contentProvider.createInlineEntry(coverLetter.getApplicationTopic()));
        //                generatedDocumentContent.put("<text>", contentGenerator.createMultiLineEntry(coverLetter
        //                .getParagraphs()));

        return generatedDocumentContent;
    }

    private Applicant getApplicant()
    {
        Map<String, Object> applicantsData = (Map<String, Object>) data.get("info");

        Applicant result = new Applicant();
        result.setPersonalInfo(getPersonalInfo(applicantsData));
        result.setContact(getContact(true, (Map<String, String>) applicantsData.get("contact")));
        result.setHobbies((List<String>) data.get("hobbies"));
        result.setLanguages(mapLanguages((List<Map<String, Object>>) applicantsData.get("spokenLanguages")));

        CareerMapper careerMapper = new CareerMapper();
        result.setCareer(careerMapper.getCareerForApplicant((Map<String, Map<String, Object>>) data.get("career")));

        SkillsMapper skillsMapper = new SkillsMapper();
        result.setSkills(skillsMapper.getSkillsForApplicant((Map<String, Map<String, Object>>) data.get("skills")));

        ProjectMapper projectMapper = new ProjectMapper();
        result.setProjects(
            projectMapper.getProjectsForApplicant((Map<String, Map<String, Object>>) data.get("projects")));

        return result;
    }

    private CoverLetter getCoverLetter()
    {
        Map<String, Object> coverLetter = (Map<String, Object>) data.get("coverLetter");
        CoverLetter result = new CoverLetter();
        result.setApplicationTopic(coverLetter.get("applicationTopic").toString());
        result.setParagraphs((List<String>) coverLetter.get("paragraphs"));
        result.setRecipient(getRecipientData());
        return result;
    }

    private PersonalInfo getPersonalInfo(Map<String, Object> applicantsData)
    {
        PersonalInfo result = new PersonalInfo();
        result.setFirstName(applicantsData.get("firstName").toString());
        result.setLastName(applicantsData.get("lastName").toString());
        result.setJobTitle(applicantsData.get("jobtitle").toString());
        result.setDateOfBirth(applicantsData.get("dateOfBirth").toString());
        result.setPlaceOfBirth(applicantsData.get("placeOfBirth").toString());
        result.setFamilyStatus(applicantsData.get("familyStatus").toString());
        return result;
    }

    private List<Language> mapLanguages(List<Map<String, Object>> languages)
    {
        List<Language> result = new ArrayList<>();
        languages.forEach(l -> result.add(
            new Language(l.get("language").toString(), Integer.parseInt(l.get("rating").toString()),
                l.get("levelDesc").toString())));
        result.sort(Comparator.comparingInt(Language::getRating).reversed());
        return result;
    }

    private Contact getContact(boolean applicatorContact, Map<String, String> data)
    {
        Contact result = new Contact();
        if (applicatorContact)
        {
            result.setEmail(data.get("email"));
            result.setPhoneNumber(data.get("phone"));
            result.setWebsite(data.get("website"));
        }
        result.setAddress(data.get("address"));
        String[] cityInfo = data.get("city").split(" ");
        result.setZipCode(cityInfo[0]);
        result.setCity(cityInfo[1]);

        return result;
    }

    private Recipient getRecipientData()
    {
        Map<String, String> recipient = (Map<String, String>) data.get("recipient");
        Recipient result = new Recipient();
        result.setCompany(recipient.get("company"));
        result.setContactPerson(!"".equals(recipient.get("contactPerson")) ? recipient.get("contactPerson") : null);
        result.setContact(
            getContact(false, Map.of("city", recipient.get("city"), "address", recipient.get("address"))));
        return result;
    }
}