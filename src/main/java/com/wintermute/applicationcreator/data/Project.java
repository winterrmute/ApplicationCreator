package com.wintermute.applicationcreator.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
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
