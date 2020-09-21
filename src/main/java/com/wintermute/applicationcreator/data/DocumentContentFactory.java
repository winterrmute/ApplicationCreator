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
 * Generates information in statements tex document content organized by replace tags.
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
        ReplaceTagHandlerGenerator contentGenerator = new ReplaceTagHandlerGenerator();

        Applicant applicant = getApplicant();

        generatedDocumentContent.put(":header:", contentGenerator.createInlineEntry(
            applicant.getPersonalInfo().getFirstName() + "\\\\" + applicant.getPersonalInfo().getLastName() + "\\\\"
                + applicant.getPersonalInfo().getJobTitle() + "}{pics/pic.jpg}"));
        generatedDocumentContent.put(":header_date:", contentGenerator.createInlineEntry(
            applicant.getContact().getCity() + ", den " + LocalDate
                .now()
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))));
        generatedDocumentContent.put(":name:",
            contentGenerator.createInlineEntry(applicant.getPersonalInfo().getFullName()));
        generatedDocumentContent.put("address",
            contentGenerator.createInlineEntry(applicant.getContact().getAddress()));
        generatedDocumentContent.put(":website:",
            contentGenerator.createInlineEntry(applicant.getContact().getWebsite()));
        generatedDocumentContent.put(":street:",
            contentGenerator.createInlineEntry(applicant.getContact().getAddress()));
        generatedDocumentContent.put(":city:",
            contentGenerator.createInlineEntry(applicant.getContact().getCityWithZipcode()));
        generatedDocumentContent.put(":phonenumber:",
            contentGenerator.createInlineEntry(applicant.getContact().getPhoneNumber()));
        generatedDocumentContent.put(":email:",
            contentGenerator.createInlineEntry(applicant.getContact().getPhoneNumber()));
        generatedDocumentContent.put(":career:", contentGenerator.createCareerEntries(applicant.getCareer()));
        generatedDocumentContent.put(":skills:",
            contentGenerator.createSkillsEntries(applicant.getSkills())); //TODO: handle skills
        generatedDocumentContent.put(":projects:", contentGenerator.createProjectEntries(applicant.getProjects()));
        generatedDocumentContent.put(":hobbies:", contentGenerator.createInlineEntry("")); //TODO: handle projects
        generatedDocumentContent.put(":languages:",
            contentGenerator.createLanguageEntries(applicant.getLanguages())); //TODO: handle projects

        CoverLetter coverLetter = getCoverLetter();
        Recipient recipient = coverLetter.getRecipient();
        generatedDocumentContent.put(":company:", contentGenerator.createInlineEntry(recipient.getCompany()));
        generatedDocumentContent.put(":contactPerson:",
            contentGenerator.createInlineEntry(recipient.getContactPerson()));
        generatedDocumentContent.put(":recipientAddress:",
            contentGenerator.createInlineEntry(recipient.getContact().getAddress()));
        generatedDocumentContent.put(":recipientCity:",
            contentGenerator.createInlineEntry(recipient.getContact().getCityWithZipcode()));
        generatedDocumentContent.put(":applicationTopic:",
            contentGenerator.createInlineEntry(coverLetter.getApplicationTopic()));
        //        generatedDocumentContent.put(":text:", contentGenerator.createMultiLineEntry(coverLetter
        //        .getParagraphs()));

        return generatedDocumentContent;
    }

    /**
     * Collects information about the applicant. {@link Applicant} holds the pojo information
     *
     * @return {@link Applicant} filled with data.
     */
    public Applicant getApplicant()
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

    /**
     * Collects information about {@link CoverLetter} and organizes it.
     *
     * @return {@link CoverLetter} filled with data.
     */
    public CoverLetter getCoverLetter()
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