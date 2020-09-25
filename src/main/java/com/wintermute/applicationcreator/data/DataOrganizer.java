package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.data.mapper.CareerMapper;
import com.wintermute.applicationcreator.data.mapper.ProjectMapper;
import com.wintermute.applicationcreator.data.mapper.SkillsMapper;
import com.wintermute.applicationcreator.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Organizes data into objects.
 *
 * @author wintermute
 */
public class DataOrganizer {

    /**
     * @param applicantData containing all information from the application file.
     * @return organized applicant data.
     */
    public Applicant getApplicant(Map<String, Object> applicantData) {
        Map<String, Object> applicantsData = (Map<String, Object>) applicantData.get("info");

        Applicant result = new Applicant();
        result.setPersonalInfo(getPersonalInfo(applicantsData));
        result.setContact(getContact(true, (Map<String, String>) applicantsData.get("contact")));
        result.setHobbies(((Map<String, List<String>>) applicantData.get("info")).get("hobbies"));
        result.setLanguages(mapLanguages((List<Map<String, Object>>) applicantsData.get("spokenLanguages")));

        CareerMapper careerMapper = new CareerMapper();
        result.setCareer(careerMapper.getCareerForApplicant((Map<String, Map<String, Object>>) applicantData.get("career")));

        SkillsMapper skillsMapper = new SkillsMapper();
        result.setSkills(skillsMapper.getSkillsForApplicant((Map<String, Map<String, Object>>) applicantData.get("skills")));

        ProjectMapper projectMapper = new ProjectMapper();
        result.setProjects(
                projectMapper.getProjectsForApplicant((Map<String, Map<String, Object>>) applicantData.get("projects")));

        return result;
    }

    /**
     * @param coverLetter data containing cover letter information.
     * @param recipient data containing recipient information.
     * @return organized data for cover letter.
     */
    public CoverLetter getCoverLetter(Map<String, Object> coverLetter, Map<String, String> recipient) {
        CoverLetter result = new CoverLetter();
        result.setApplicationTopic(coverLetter.get("applicationTopic").toString());
        result.setParagraphs((List<String>) coverLetter.get("paragraphs"));
        result.setRecipient(getRecipientData(recipient));
        return result;
    }

    private PersonalInfo getPersonalInfo(Map<String, Object> applicantsData) {
        PersonalInfo result = new PersonalInfo();
        result.setFirstName(applicantsData.get("firstName").toString());
        result.setLastName(applicantsData.get("lastName").toString());
        result.setJobTitle(applicantsData.get("jobtitle").toString());
        result.setDateOfBirth(applicantsData.get("dateOfBirth").toString());
        result.setPlaceOfBirth(applicantsData.get("placeOfBirth").toString());
        result.setFamilyStatus(applicantsData.get("familyStatus").toString());
        return result;
    }

    private List<Language> mapLanguages(List<Map<String, Object>> languages) {
        List<Language> result = new ArrayList<>();
        languages.forEach(l -> result.add(
                new Language(l.get("language").toString(), Integer.parseInt(l.get("rating").toString()),
                        l.get("levelDesc").toString())));
        result.sort(Comparator.comparingInt(Language::getRating).reversed());
        return result;
    }

    private Contact getContact(boolean applicatorContact, Map<String, String> data) {
        Contact result = new Contact();
        if (applicatorContact) {
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

    private Recipient getRecipientData(Map<String, String> recipient) {
        Recipient result = new Recipient();
        result.setCompany(recipient.get("company"));
        result.setContactPerson(!"".equals(recipient.get("contactPerson")) ? recipient.get("contactPerson") : null);
        result.setContact(
                getContact(false, Map.of("city", recipient.get("city"), "address", recipient.get("address"))));
        return result;
    }
}
