package com.wintermute.applicationcreator.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Represents project with all important information.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Project extends ElementWithTimePeriod
{
    private String position;
    private String description;
    private String githubLink;
    private List<String> programmingLanguages;
    private List<String> frameworks;
    private List<String> tools;
}
