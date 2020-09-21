package com.wintermute.applicationcreator.data.mapper;

import com.wintermute.applicationcreator.model.CategoryGroup;
import com.wintermute.applicationcreator.model.Project;

import java.util.List;
import java.util.Map;

/**
 * Organizes career by category and time period.
 *
 * @author wintermute
 */
public class ProjectMapper extends CategoryMapper
{
    public Map<CategoryGroup, List<Project>> getProjectsForApplicant(Map<String, Map<String, Object>> data)
    {
        return getCategoryGroupForCategory(data, this::getProject);
    }

    private Project getProject(Map<String, Object> projectInfo)
    {
        Project result = new Project();
        result.setFrom(projectInfo.get("from").toString());
        result.setUntil(projectInfo.get("until").toString());
        result.setTitle(projectInfo.get("summary").toString());
        result.setDescription(projectInfo.get("description").toString());
        result.setPosition(projectInfo.get("position").toString());
        result.setGithubLink(projectInfo.get("githubLink") != null ? projectInfo.get("githubLink").toString() : null);
        Map<String, List<String>> tools = (Map<String, List<String>>) projectInfo.get("tools");
        result.setProgrammingLanguages(tools.get("languages").size() == 0 ? null : tools.get("languages"));
        result.setFrameworks(tools.get("frameworks").size() == 0 ? null : tools.get("frameworks"));
        result.setTools(tools.get("other").size() == 0 ? null : tools.get("other"));
        return result;
    }
}
