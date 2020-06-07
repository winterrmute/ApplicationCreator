package com.wintermute.applicationcreator.collector;

import com.wintermute.applicationcreator.applicationData.Applicant;
import com.wintermute.applicationcreator.applicationData.Career;
import com.wintermute.applicationcreator.applicationData.Contact;
import com.wintermute.applicationcreator.applicationData.CoverLetter;
import com.wintermute.applicationcreator.applicationData.Language;
import com.wintermute.applicationcreator.applicationData.Project;
import com.wintermute.applicationcreator.applicationData.Recipient;
import com.wintermute.applicationcreator.applicationData.Skill;
import com.wintermute.applicationcreator.applicationData.WithDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for organizing data read from json to
 *
 * @author wintermute
 */
public class ObjectMapper
{
    private final Map<String, Object> data;
    private final Map<String, Object> applicantsInfo;

    public ObjectMapper(Map<String, Object> data)
    {
        this.data = data;
        this.applicantsInfo = (Map<String, Object>) data.get("info");
    }

    /**
     * Collects information about the applicant. {@link Applicant} holds the pojo information
     *
     * @return {@link Applicant} filled with data.
     */
    public Applicant getApplicant()
    {
        Applicant result = new Applicant();
        result.setFirstName(applicantsInfo.get("firstName").toString());
        result.setLastName(applicantsInfo.get("lastName").toString());
        result.setJobTitle(applicantsInfo.get("jobtitle").toString());
        result.setDateOfBirth(applicantsInfo.get("dateOfBirth").toString());
        result.setPlaceOfBirth(applicantsInfo.get("placeOfBirth").toString());
        result.setFamilyStatus(applicantsInfo.get("familyStatus").toString());

        result.setContact(getContact(true, (Map<String, String>) applicantsInfo.get("contact")));
        result.setHobbies((List<String>) applicantsInfo.get("hobbies"));
        result.setSoftSkills((List<String>) data.get("softSkills"));
        result.setLanguages(mapLanguages());
        result.setCareer(mapCareer());
        result.setSkills(mapSkills());
        result.setProjects(mapProjects());
        return result;
    }

    /**
     * Maps information for creating {@link CoverLetter}.
     *
     * @return {@link CoverLetter} filled with data.
     */
    public CoverLetter getCoverLetter()
    {
        Map<String, Object> coverLetter = (Map<String, Object>) data.get("coverLetter");
        CoverLetter result = new CoverLetter();
        result.setApplicationAs(coverLetter.get("applicationTopic").toString());
        result.setParagraphs((List<String>) coverLetter.get("paragraphs"));
        result.setRecipient(mapRecipient());
        return result;
    }

    private List<Language> mapLanguages()
    {
        List<Map<String, Object>> spokenLanguages = (List<Map<String, Object>>) applicantsInfo.get("spokenLanguages");
        List<Language> result = new ArrayList<>();

        spokenLanguages.forEach(l -> result.add(
            new Language(l.get("language").toString(), Integer.parseInt(l.get("rating").toString()),
                l.get("levelDesc").toString())));
        result.sort(Comparator.comparingInt(Language::getRating).reversed());
        return result;
    }

    private Map<String, Map<String, List<Skill>>> mapSkills()
    {
        Map<String, Map<String, List<Skill>>> result = new HashMap<>();
        Map<String, List<Skill>> skillsGroup;
        Map<String, List<Skill>> skills = mapDataByType("skills", Skill.class);
        for (String key : skills.keySet())
        {
            skillsGroup = new HashMap<>();
            for (Skill target : skills.get(key))
            {
                skillsGroup.computeIfAbsent(target.getCategory(), l -> new ArrayList<>());
                skillsGroup.get(target.getCategory()).add(target);
                skillsGroup.get(target.getCategory()).sort(Comparator.comparingInt(Skill::getRating).reversed());
                result.put(key, skillsGroup);
            }
        }
        return result;
    }

    private Map<String, List<Career>> mapCareer()
    {
        return mapDataByType("career", Career.class);
    }

    private Map<String, List<Project>> mapProjects()
    {
        return mapDataByType("projects", Project.class);
    }

    private Career getMappedCareer(Map<String, Object> careerInfo, String careerType)
    {
        Career result = new Career();
        result.setFrom(careerInfo.get("from").toString());
        result.setUntil(careerInfo.get("until").toString());
        if ("professionalCareer".equals(careerType))
        {
            result.setTitle(careerInfo.get("company").toString());
            result.setJob(careerInfo.get("job").toString());
            result.setDescription(careerInfo.get("description").toString());
        } else if ("educationalCareer".equals(careerType))
        {
            result.setTitle(careerInfo.get("school").toString());
            result.setDescription(careerInfo.get("graduation").toString());
        }
        return result;
    }

    private Skill getMappedSkill(Map<String, Object> skillInfo, String skillType)
    {
        Skill result = new Skill();
        result.setName(skillInfo.get("description").toString());
        result.setRating(Integer.parseInt(skillInfo.get("rating").toString()));
        result.setCategory(skillInfo.get("category").toString());
        result.setType(skillType);
        return result;
    }

    private Project getMappedProject(Map<String, Object> projectInfo)
    {
        Project result = new Project();
        result.setFrom(projectInfo.get("from").toString());
        result.setUntil(projectInfo.get("until").toString());
        result.setTitle(projectInfo.get("summary").toString());
        result.setDescription(projectInfo.get("description").toString());
        result.setPosition(projectInfo.get("position").toString());
        result.setGithubLink(projectInfo.get("githubLink") != null ? projectInfo.get("githubLink").toString() : null);
        Map<String, List<String>> tools = (Map<String, List<String>>) projectInfo.get("tools");
        result.setProgrammingLanguages("".equals(tools.get("languages").get(0)) ? null : tools.get("languages"));
        result.setFrameworks("".equals(tools.get("frameworks").get(0)) ? null : tools.get("frameworks"));
        result.setTools("".equals(tools.get("other").get(0)) ? null : tools.get("other"));
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

    private Recipient mapRecipient()
    {
        Map<String, String> recipient = (Map<String, String>) data.get("recipient");
        Recipient result = new Recipient();
        result.setCompany(recipient.get("company"));
        result.setContactPerson(!"".equals(recipient.get("contactPerson")) ? recipient.get("contactPerson") : null);
        result.setContact(
            getContact(false, Map.of("city", recipient.get("city"), "address", recipient.get("address"))));
        return result;
    }

    private <T> Map<String, List<T>> mapDataByType(String dataSection, Class<T> clazz)
    {
        String classPrefix = "com.wintermute.applicationcreator.applicationData.";
        Map<String, List<T>> result = new HashMap<>();
        Map<String, List<Map<String, Object>>> extractedData =
            (Map<String, List<Map<String, Object>>>) data.get(dataSection);

        for (Map.Entry<String, List<Map<String, Object>>> dataType : extractedData.entrySet())
        {
            List<T> dataContent = new ArrayList<>();
            if (!dataType.getKey().equals("soft")) //TODO: reorganize data so this if-clause is not needed anymore
            {
                for (Map<String, Object> dataInfo : dataType.getValue())
                {
                    if ((classPrefix + "Skill").equals(clazz.getName()))
                    {
                        dataContent.add((T) getMappedSkill(dataInfo, dataType.getKey()));
                    } else if ((classPrefix + "Career").equals(clazz.getName()))
                    {
                        dataContent.add((T) getMappedCareer(dataInfo, dataType.getKey()));
                    } else if ((classPrefix + "Project").equals(clazz.getName()))
                    {
                        dataContent.add((T) getMappedProject(dataInfo));
                    }
                }
                if (!(classPrefix + "Skill").equals(clazz.getName()))
                {
                    sortByDate(dataContent);
                }
                result.put(dataType.getKey(), dataContent);
            }
        }
        return result;
    }

    private <T> void sortByDate(List<T> target)
    {
        DateComparator dateComparator = new DateComparator();
        target.sort((Comparator) dateComparator);
    }
}

/**
 * Comparator for data containing start and end date. Compares reverse so the newest activities are on top.
 *
 * @author wintermute
 */
class DateComparator implements Comparator<WithDate>
{
    public int compare(WithDate p, WithDate q)
    {
        if (getDate(p.getFrom()).before(getDate(q.getFrom())))
        {
            return 1;
        } else if (getDate(p.getFrom()).after(getDate(q.getFrom())))
        {
            return -1;
        } else
        {
            return 0;
        }
    }

    Date getDate(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        Date result;
        try
        {
            result = sdf.parse(date);
        } catch (ParseException e)
        {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
}
