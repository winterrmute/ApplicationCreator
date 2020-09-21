package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.model.Career;
import com.wintermute.applicationcreator.model.CategoryGroup;
import com.wintermute.applicationcreator.model.Project;
import com.wintermute.applicationcreator.model.Skill;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Generates line entry with filled data which is passed into final document.
 */
public class DocumentContentParser
{
    /**
     * @param target data set ordered by category.
     * @param section to create new tex document syntax.
     * @param isTable to define whether the document entry is a table element.
     * @param <T> type of applicant data.
     * @return parsed data to document tex format.
     */
    <T> StringBuilder parseEntryWithCategory(Map<CategoryGroup, List<T>> target, String section,
                                                     boolean isTable)
    {
        StringBuilder result = new StringBuilder();
        target.forEach((category, content) ->
        {
            result.append(parseHeader(section, category.getTitle()));
            result.append(isTable ? "\\begin{longtable}{p{11em}| p{25em}}\n" : "");
            result.append(parseContent(content));
            result.append(isTable ? "\n\\end{longtable}\n\n" : "");
        });
        return result;
    }

    /**
     * @param target ordered skills by category.
     * @return parsed data to document tex format.
     */
    String parseSkills(Map<CategoryGroup, List<Skill>> target)
    {
        StringBuilder result = new StringBuilder();
        target.forEach((category, skills) -> {
            result.append(parseHeader("Skills", category.getTitle()));
            result.append("\\begin{longtable}{p{11em}| p{25em}}\n");
            Map<String, List<Skill>> bySkillCategory = new HashMap<>();
            //TODO: collect skills by skill category
        });
        return result.toString();
    }

    private <T> String parseContent(List<T> content)
    {
        StringBuilder result = new StringBuilder();
        Iterator<T> iterator = content.iterator();
        while (iterator.hasNext())
        {
            T next = iterator.next();
            if (next instanceof Project)
            {
                result.append(parseProject((Project) next)).append(iterator.hasNext() ? "\n" : "");
            } else if (next instanceof Career)
            {
                result.append(parseCareer((Career) next)).append(iterator.hasNext() ? "\n" : "");
            }
        }
        return result.toString();
    }

    private String parseProject(Project project)
    {
        StringBuilder result = new StringBuilder("\\begin{longtable}{p{11em}| p{25em}}\n")
            .append(parseActivity(project.getFrom() + " - " + project.getUntil(), project.getTitle())).append("\\\\n")
            .append(parseSingleItem("position", project.getPosition()))
            .append(parseSingleItem("description", project.getDescription()));
        result.append(
            project.getGithubLink() != null ? parseCommaSeparatedList("github", List.of(project.getGithubLink())) : "");
        result.append(project.getProgrammingLanguages() != null ? parseCommaSeparatedList("languages",
            project.getProgrammingLanguages()) : "");
        result.append(
            project.getFrameworks() != null ? parseCommaSeparatedList("frameworks", project.getFrameworks()) : "");
        result.append(project.getTools() != null ? parseCommaSeparatedList("tools", project.getTools()) : "");
        return result.append("\\end{longtable}\n\n").toString();
    }

    private String parseCareer(Career career)
    {
        return parseActivity(career.getFrom() + " - " + career.getUntil(), career.getTitle()) + "{"
            + career.getDescription() + "}\\\\";
    }

    private String parseHeader(String section, String category)
    {
        return "\\customsection{" + section.substring(0, 3) + "}{" + section.substring(3) + " (" + category + ")}\n";
    }


    private String parseActivity(String fieldHeader, String activity)
    {
        return "\\columntitle{" + fieldHeader + "} & \\activity{" + activity + "}";
    }

    private String parseSingleItem(String fieldHeader, String item)
    {
        return parseColumnSubTitle(fieldHeader) + " & \\singleitem{" + item + "}\\\\\n";
    }

    private String parseColumnSubTitle(String fieldHeader)
    {
        return "\t\\columnsubtitle{" + fieldHeader + "}";
    }

    private String parseCommaSeparatedList(String title, List<String> elements)
    {
        StringBuilder result = new StringBuilder(parseColumnSubTitle(title)).append(" & \\commaseparatedlist{");
        Iterator<String> iterator = elements.iterator();
        while (iterator.hasNext())
        {
            result.append(iterator.next()).append(iterator.hasNext() ? "," : "");
        }
        return result.append("}\\\\\n").toString();
    }

//    private String getTimePeriod(String start, String end, String title)
//    {
//        return "\t\\columntitle{" + start + "-" + end + "} & \\activity{" + title + "}" + (title.length() > 37
//                                                                                           ? "\\\\\\\\" : "\\\\");
//    }
}
