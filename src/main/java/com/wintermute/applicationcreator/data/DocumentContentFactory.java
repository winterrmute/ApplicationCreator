package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.datamodel.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Function;

/**
 * Generates information in statements tex document content organized by replace tags.
 *
 * @author wintermute
 */
public class DocumentContentFactory {
    private final Map<String, Object> data;
    private final Map<String, Function<String, String>> generatedDocumentContent;

    public DocumentContentFactory(Map<String, Object> data) {
        this.data = data;
        //TODO: notify the user when data is empty.
        generatedDocumentContent = new HashMap<>();
    }

    public Map<String, Function<String, String>> getDocumentContent() {
        ReplaceTagHandlerGenerator contentGenerator = new ReplaceTagHandlerGenerator();

        Applicant applicant = getApplicant();

        generatedDocumentContent.put(":header:", contentGenerator.createInlineEntry(applicant.getPersonalInfo().getFirstName() + "\\\\" + applicant.getPersonalInfo().getLastName()
                + "\\\\" + applicant.getPersonalInfo().getJobTitle() + "}{pics/pic.jpg}"));
        generatedDocumentContent.put(":header_date:", contentGenerator.createInlineEntry(applicant.getContact().getCity() + ", den " + LocalDate
                .now()
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))));
        generatedDocumentContent.put(":name:", contentGenerator.createInlineEntry(applicant.getPersonalInfo().getFullName()));
        generatedDocumentContent.put("address", contentGenerator.createInlineEntry(applicant.getContact().getAddress()));
        generatedDocumentContent.put(":website:", contentGenerator.createInlineEntry(applicant.getContact().getWebsite()));
        generatedDocumentContent.put(":street:", contentGenerator.createInlineEntry(applicant.getContact().getAddress()));
        generatedDocumentContent.put(":city:", contentGenerator.createInlineEntry(applicant.getContact().getCityWithZipcode()));
        generatedDocumentContent.put(":phonenumber:", contentGenerator.createInlineEntry(applicant.getContact().getPhoneNumber()));
        generatedDocumentContent.put(":email:", contentGenerator.createInlineEntry(applicant.getContact().getPhoneNumber()));
        generatedDocumentContent.put(":career:", contentGenerator.createCareerEntries(applicant.getCareer()));
        generatedDocumentContent.put(":education:", contentGenerator.createCareerEntries(applicant.getEducation()));
        generatedDocumentContent.put(":skills:", contentGenerator.createInlineEntry("")); //TODO: handle skills
        generatedDocumentContent.put(":projects:", contentGenerator.createProjectEntries(applicant.getProjects())); //TODO: handle projects
        generatedDocumentContent.put(":hobbies:", contentGenerator.createInlineEntry("")); //TODO: handle projects
        generatedDocumentContent.put(":languages:", contentGenerator.createLanguageEntries(applicant.getLanguages())); //TODO: handle projects

        CoverLetter coverLetter = getCoverLetter();
        Recipient recipient = coverLetter.getRecipient();
        generatedDocumentContent.put(":company:", contentGenerator.createInlineEntry(recipient.getCompany()));
        generatedDocumentContent.put(":contactPerson:", contentGenerator.createInlineEntry(recipient.getContactPerson()));
        generatedDocumentContent.put(":recipientAddress:", contentGenerator.createInlineEntry(recipient.getContact().getAddress()));
        generatedDocumentContent.put(":recipientCity:", contentGenerator.createInlineEntry(recipient.getContact().getCityWithZipcode()));
        generatedDocumentContent.put(":applicationTopic:", contentGenerator.createInlineEntry(coverLetter.getApplicationTopic()));
//        generatedDocumentContent.put(":text:", contentGenerator.createMultiLineEntry(coverLetter.getParagraphs()));

        return generatedDocumentContent;
    }

    /**
     * Collects information about the applicant. {@link Applicant} holds the pojo information
     *
     * @return {@link Applicant} filled with data.
     */
    public Applicant getApplicant() {
        Map<String, Object> applicantsData = (Map<String, Object>) data.get("info");

        Applicant result = new Applicant();
        result.setPersonalInfo(getPersonalInfo(applicantsData));
        result.setContact(getContact(true, (Map<String, String>) applicantsData.get("contact")));
        result.setHobbies((List<String>) data.get("hobbies"));
        result.setSoftSkills((List<String>) data.get("softSkills"));
        result.setLanguages(mapLanguages((List<Map<String, Object>>) applicantsData.get("spokenLanguages")));
        result.setCareer(getCareerForApplicant());
        result.setEducation(null);
//                result.setCareer(mapCareer());
        result.setSkills(mapSkills());
        result.setProjects(getProjectsForApplicant());
        return result;
    }

    /**
     * Collects information about {@link CoverLetter} and organizes it.
     *
     * @return {@link CoverLetter} filled with data.
     */
    public CoverLetter getCoverLetter() {
        Map<String, Object> coverLetter = (Map<String, Object>) data.get("coverLetter");
        CoverLetter result = new CoverLetter();
        result.setApplicationTopic(coverLetter.get("applicationTopic").toString());
        result.setParagraphs((List<String>) coverLetter.get("paragraphs"));
        result.setRecipient(mapRecipient());
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

    private Map<String, Map<String, List<Skill>>> mapSkills() {
        Map<String, Map<String, List<Skill>>> result = new HashMap<>();
        Map<String, List<Skill>> skillsGroup;
        Map<String, List<Skill>> skills = mapDataByType("skills", Skill.class);
        for (String key : skills.keySet()) {
            skillsGroup = new HashMap<>();
            for (Skill target : skills.get(key)) {
                skillsGroup.computeIfAbsent(target.getCategory(), l -> new ArrayList<>());
                skillsGroup.get(target.getCategory()).add(target);
                skillsGroup.get(target.getCategory()).sort(Comparator.comparingInt(Skill::getRating).reversed());
                result.put(key, skillsGroup);
            }
        }
        return result;
    }

    private Map<String, List<Career>> mapCareer() {
        return mapDataByType("career", Career.class);
    }

    private List<Career> getEducationForApplicant() {
        List<Career> result = new ArrayList<>();
        HashMap<String, List<Object>> projects = (HashMap<String, List<Object>>) data.get("career");
        projects.forEach((category, desc) -> {
            Career career = new Career(category);
            result.add(getMappedCareer(career, (Map<String, Object>) desc.get(0)));
        });
        return result;
    }

    private List<Career> getCareerForApplicant() {
        List<Career> result = new ArrayList<>();
        HashMap<String, List<Object>> careers = (HashMap<String, List<Object>>) data.get("career");
        careers.forEach((category, desc) -> {
            Career career = new Career(category);
            result.add(getMappedCareer(career, (Map<String, Object>) desc.get(0)));
        });
        //TODO: Fix result
        return result;
    }

    private List<Project> getProjectsForApplicant() {
        List<Project> result = new ArrayList<>();
        HashMap<String, List<Object>> projects = (HashMap<String, List<Object>>) data.get("projects");
        projects.forEach((category, desc) -> {
            Project project = new Project(category);
            result.add(getMappedProject(project, (Map<String, Object>) desc.get(0)));
        });
        return result;
    }

    private Career getMappedCareer(Career result, Map<String, Object> careerInfo) {
        result.setFrom(careerInfo.get("from").toString());
        result.setUntil(careerInfo.get("until").toString());
        if ("professionalCareer".equals(result.getCategory())) {
            result.setTitle(careerInfo.get("company").toString());
            result.setJob(careerInfo.get("job").toString());
            result.setDescription(careerInfo.get("description").toString());
        } else if ("educationalCareer".equals(result.getCategory())) {
            result.setTitle(careerInfo.get("school").toString());
            result.setDescription(careerInfo.get("graduation").toString());
        }
        return result;
    }

    private Skill getMappedSkill(Map<String, Object> skillInfo, String skillType) {
        Skill result = new Skill();
        result.setName(skillInfo.get("description").toString());
        result.setRating(Integer.parseInt(skillInfo.get("rating").toString()));
        result.setCategory(skillInfo.get("category").toString());
        result.setType(skillType);
        return result;
    }

    private Project getMappedProject(Project result, Map<String, Object> projectInfo) {
        result.setFrom(projectInfo.get("from").toString());
        result.setUntil(projectInfo.get("until").toString());
        result.setTitle(projectInfo.get("summary").toString());
        result.setDescription(projectInfo.get("description").toString());
        result.setPosition(projectInfo.get("position").toString());
        result.setGithubLink(projectInfo.get("githubLink") != null ? projectInfo.get("githubLink").toString() : null);
        Map<String, List<String>> tools = (Map<String, List<String>>) projectInfo.get("tools");
        result.setProgrammingLanguages(tools.get("languages").size() == 0 ? null : tools.get("languages"));
        result.setFrameworks(tools.get("frameworks").size() == 0 ? null : tools.get("frameworks"));
        result.setTools(tools.get("other").size() == 0 ? null : tools.get("other"));
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

    private Recipient mapRecipient() {
        Map<String, String> recipient = (Map<String, String>) data.get("recipient");
        Recipient result = new Recipient();
        result.setCompany(recipient.get("company"));
        result.setContactPerson(!"".equals(recipient.get("contactPerson")) ? recipient.get("contactPerson") : null);
        result.setContact(
                getContact(false, Map.of("city", recipient.get("city"), "address", recipient.get("address"))));
        return result;
    }

    private <T> Map<String, List<T>> mapDataByType(String dataSection, Class<T> clazz) {
        String classPrefix = "com.wintermute.applicationcreator.data.";
        Map<String, List<T>> result = new HashMap<>();
        Map<String, List<Map<String, Object>>> extractedData =
                (Map<String, List<Map<String, Object>>>) data.get(dataSection);

        for (Map.Entry<String, List<Map<String, Object>>> dataType : extractedData.entrySet()) {
            List<T> dataContent = new ArrayList<>();
            if (!dataType.getKey().equals("soft")) //TODO: reorganize data so this if-clause is not needed anymore
            {
                for (Map<String, Object> dataInfo : dataType.getValue()) {
                    if ((classPrefix + "Skill").equals(clazz.getName())) {
                        dataContent.add((T) getMappedSkill(dataInfo, dataType.getKey()));
                    }
                }
                if (!(classPrefix + "Skill").equals(clazz.getName())) {
                    sortByDate(dataContent);
                }
                result.put(dataType.getKey(), dataContent);
            }
        }
        return result;
    }

    private <T> void sortByDate(List<T> target) {
        DateComparator dateComparator = new DateComparator();
        target.sort((Comparator) dateComparator);
    }
}

/**
 * Comparator for data containing start and end date. Compares reverse so the newest activities are on top.
 *
 * @author wintermute
 */
class DateComparator implements Comparator<WithDate> {
    public int compare(WithDate p, WithDate q) {
        if (getDate(p.getFrom()).before(getDate(q.getFrom()))) {
            return 1;
        } else if (getDate(p.getFrom()).after(getDate(q.getFrom()))) {
            return -1;
        } else {
            return 0;
        }
    }

    Date getDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        Date result;
        try {
            result = sdf.parse(date);
        } catch (ParseException e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
}
