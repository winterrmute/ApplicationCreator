package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.datamodel.Career;
import com.wintermute.applicationcreator.datamodel.Project;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Generates line entry with filled data which is passed into final document.
 */
public class DocumentContentParser {

    /**
     * @param project containing all data.
     * @return extracted project data and prepares entry to fit into document.
     */
    String getProject(Project project) {
        StringBuilder result = new StringBuilder(getTimePeriod(project.getFrom(), project.getUntil(), project.getTitle()));

        if (!"".equals(project.getPosition())) {
            result.append(parseSingleEntryWithHighlitedTitle("position", project.getPosition()));
        }

        result.append(parseSingleEntryWithHighlitedTitle("description", project.getDescription()));

        result.append(parseMultiLinedListWithHighlightedCategory(Collections.singletonMap("languages", project.getProgrammingLanguages())));
        result.append(parseMultiLinedListWithHighlightedCategory(Collections.singletonMap("frameworks", project.getFrameworks())));
        result.append(parseMultiLinedListWithHighlightedCategory(Collections.singletonMap("tools", project.getTools())));

        if (!"".equals(project.getGithubLink()) && project.getGithubLink() != null) {
            result.append(parseSingleEntryWithHighlitedTitle("github Link", project.getGithubLink()));
        }
        //TODO: build long table out of projects
//        result.append("\n").append("\\end{longtable}\n\n");

        return result.toString();
    }

    String getCareer(Career career) {
        StringBuilder result = new StringBuilder(getTimePeriod(career.getFrom(), career.getUntil(), career.getTitle()));
        if ("professionalCareer".equals(career.getCategory())){
            result.append("{").append(career.getJob()).append("}");
        }
        result.append("{").append(career.getDescription()).append("}\\\\").append("\n\n");
        return result.toString();
    }

    private String getTimePeriod(String start, String end, String title) {
        return "\\columntitle{" + start + "-" + end + "} & \\activity{" + title + "}" + (title.length() > 37 ? "\\\\\\\\" : "\\\\");
    }

    private String parseMultiLinedListWithHighlightedCategory(Map<String, List<String>> category) {
        try {
            StringBuilder result = new StringBuilder();
            category.forEach((key, value) -> {
                result.append("\n\t\\columnsubtitle{").append(key).append("} & ");
                value.forEach(e -> result.append("\\\\singleitem{\"").append(e).append("}\\\\\n"));
            });
            return result.toString();
        } catch (NullPointerException e) {
            return "";
        }
    }

    private String parseSingleEntryWithHighlitedTitle(String key, String value) {
        return "\n\t\\columnsubtitle{" + key + "} & " + "\\singleitem{" + value + "}\\\\";
    }

}
