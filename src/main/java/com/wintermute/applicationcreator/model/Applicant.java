package com.wintermute.applicationcreator.model;

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
//    private Map<String, List<Career>> career;
    private Map<CategoryGroup, List<Career>> career;
    private Map<CategoryGroup, List<Skill>> skills;
    private Map<CategoryGroup, List<Project>> projects;
//    private Map<String,List<Project>> projects;
}
