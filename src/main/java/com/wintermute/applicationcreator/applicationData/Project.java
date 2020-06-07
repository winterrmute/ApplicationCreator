package com.wintermute.applicationcreator.applicationData;

import lombok.Data;

import java.util.List;

@Data
public class Project extends WithDate
{
    private String position;
    private String description;
    private String title;
    private String githubLink;
    private List<String> programmingLanguages;
    private List<String> frameworks;
    private List<String> tools;
}
