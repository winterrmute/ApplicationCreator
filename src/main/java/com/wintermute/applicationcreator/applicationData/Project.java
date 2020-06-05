package com.wintermute.applicationcreator.applicationData;

import lombok.Data;

import java.util.List;

@Data
public class Project extends WithDate
{
    private String summary;
    private String description;
    private String title;
    private String githubLink;
    private List<String> languages;
    private List<String> frameworks;
    private List<String> tools;
}
