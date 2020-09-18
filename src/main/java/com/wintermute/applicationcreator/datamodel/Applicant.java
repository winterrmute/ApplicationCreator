package com.wintermute.applicationcreator.datamodel;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Applicant
{
    private PersonalInfo personalInfo;
    private Contact contact;
    private List<Language> languages;
    private List<String> hobbies;
    private List<String> softSkills;
//    private Map<String, List<Career>> career;
//    private Map<String, List<Career>> career;
    private List<Career> career;
    private List<Career> education;
    private Map<String, Map<String, List<Skill>>> skills;
    private List<Project> projects;
}
