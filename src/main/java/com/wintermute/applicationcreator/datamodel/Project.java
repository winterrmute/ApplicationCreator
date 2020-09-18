package com.wintermute.applicationcreator.datamodel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor //TODO: deleteme
@EqualsAndHashCode(callSuper=false)
public class Project extends WithDate
{
    private String position;
    private String description;
    private String title;
    private String githubLink;
    private String category;
    private List<String> programmingLanguages;
    private List<String> frameworks;
    private List<String> tools;

    public Project(String category) {
        this.category = category;
    }
}
