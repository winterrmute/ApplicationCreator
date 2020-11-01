package com.wintermute.applicationcreator.model.complex;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Represents project with all its information.
 *
 * @author wintermute
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Project extends ElementWithTimePeriod
{
    private String role;
    private String githubLink;
    private List<String> programmingLanguages;
    private List<String> frameworks;
    private List<String> tools;
}
