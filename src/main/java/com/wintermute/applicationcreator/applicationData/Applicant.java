package com.wintermute.applicationcreator.applicationData;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Applicant
{
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String dateOfBirth;
    private Contact contact;
    private String placeOfBirth;
    private String familyStatus;
    private Map<String, String> languageByGrade;
    private List<String> hobbies;
    private Map<String, List<Career>> career;
    private Map<String, List<Skill>> skills;
    private Map<String, List<Project>> projects;
}
