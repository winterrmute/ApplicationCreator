package com.wintermute.applicationcreator.model;

import com.wintermute.applicationcreator.model.complex.Career;
import com.wintermute.applicationcreator.model.complex.CategoryGroup;
import com.wintermute.applicationcreator.model.complex.Project;
import com.wintermute.applicationcreator.model.complex.Skill;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Represents all information about applicant including personal information, contact and all its properties.
 *
 * @author wintermute
 */
@Data
public class Applicant
{
    private PersonalInfo personalInfo;
    private List<Language> languages;
    private List<String> hobbies;
    private Map<CategoryGroup, List<Career>> career;
    private Map<CategoryGroup, Map<CategoryGroup, List<Skill>>> skills;
    private Map<CategoryGroup, List<Project>> projects;

    @Data
    public static class PersonalInfo
    {
        private String firstName;
        private String lastName;
        private String jobTitle;
        private String dateOfBirth;
        private String placeOfBirth;
        private String familyStatus;
        private Contact contact;

        public String getFullName()
        {
            return this.getFirstName() + " " + this.getLastName();
        }
    }
}