package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.model.Career;
import com.wintermute.applicationcreator.model.CategoryGroup;
import com.wintermute.applicationcreator.model.Language;
import com.wintermute.applicationcreator.model.Project;
import com.wintermute.applicationcreator.model.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
    <T> StringBuilder getParsedContentGroupedByCategory(Map<CategoryGroup, List<T>> target, String section,
                                                        boolean isTable)
    {
        StringBuilder result = new StringBuilder();
        target.forEach((category, content) ->
        {
            result.append(getParsedHeader(section, category.getTitle()));
            result.append(isTable ? getTableHeader() : "");
            result.append(getParsedListContent(content));
            result.append(isTable ? "\n\\end{longtable}\n\n" : "");
        });
        return result;
    }

    /**
     * @param target list of languages.
     * @return parsed languages to tex document format.
     */
    String getParsedLanguages(List<Language> target)
    {
        StringBuilder result = new StringBuilder(getParsedHeader("Languages", ""));
        result.append(getTableHeader());
        target.forEach(l -> result.append(getParsedSingleItem(l.getLanguage(), l.getLevelDesc())));
        result.append("\n\\end{longtable}\n");
        return result.toString();
    }

    String getParsedHobbies(List<String> hobbies){
        StringBuilder result = new StringBuilder("\\customsection{Hob}{bies}\n\n\\commaseparatedlist{");
        hobbies.forEach(h -> result.append(h).append(", "));
        return result.append("}").toString();
    }

    /**
     * @param target ordered skills by category.
     * @return parsed data to document tex format.
     */
    String getParsedSkills(Map<CategoryGroup, List<Skill>> target)
    {
        StringBuilder result = new StringBuilder();
        target.forEach((k, v) ->
        {
            result.append(getParsedHeader("Skills", k.getTitle())).append("\n");
            Map<String, List<String>> orderedSkillsByInnerCategory = getOrderedSkillsByInnerCategory(v);
            result.append(getTableHeader());
            orderedSkillsByInnerCategory.forEach((k1, v1) ->
            {
                result.append(getParsedNewLineList(k1, v1));
            });
            result.append("\n\\end{longtable}");
        });
        return result.toString();
    }

    private Map<String, List<String>> getOrderedSkillsByInnerCategory(List<Skill> target)
    {
        Map<String, List<String>> skillsByInnerCategory = new HashMap<>();
        target.forEach(s ->
        {
            if (skillsByInnerCategory.get(s.getCategory()) == null)
            {
                skillsByInnerCategory.computeIfAbsent(s.getCategory(), r -> new ArrayList<>(List.of(s.getTitle())));
            } else
            {
                skillsByInnerCategory.get(s.getCategory()).add(s.getTitle());
            }
        });
        return skillsByInnerCategory;
    }

    private <T> String getParsedListContent(List<T> content)
    {
        StringBuilder result = new StringBuilder();
        Iterator<T> iterator = content.iterator();
        while (iterator.hasNext())
        {
            T next = iterator.next();
            if (next instanceof Project)
            {
                result.append(getParsedProject((Project) next)).append(iterator.hasNext() ? "\n" : "");
            } else if (next instanceof Career)
            {
                result.append(getParsedCareer((Career) next)).append(iterator.hasNext() ? "\n" : "");
            }
        }
        return result.toString();
    }

    private String getParsedProject(Project project)
    {
        StringBuilder result = new StringBuilder(getTableHeader())
            .append(getParsedActivity(project.getFrom() + " - " + project.getUntil(), project.getTitle()))
            .append("\\\\")
            .append(getParsedSingleItem("position", project.getPosition()))
            .append(getParsedSingleItem("description", project.getDescription()));
        result.append(
            project.getGithubLink() != null ? getParsedCommaSeparatedList("github", List.of(project.getGithubLink()))
                                            : "");
        result.append(project.getProgrammingLanguages() != null ? getParsedCommaSeparatedList("languages",
            project.getProgrammingLanguages()) : "");
        result.append(
            project.getFrameworks() != null ? getParsedCommaSeparatedList("frameworks", project.getFrameworks()) : "");
        result.append(project.getTools() != null ? getParsedCommaSeparatedList("tools", project.getTools()) : "");
        return result.append("\\end{longtable}\n\n").toString();
    }

    @NotNull
    private String getTableHeader()
    {
        return "\\begin{longtable}{p{11em}| p{25em}}\n";
    }

    private String getParsedCareer(Career career)
    {
        return getParsedActivity(career.getFrom() + " - " + career.getUntil(), career.getTitle()) + "{"
            + career.getDescription() + "}\\\\";
    }

    private String getParsedHeader(String section, String... category)
    {
        return "\\customsection{" + section.substring(0, 3) + "}{" + section.substring(3) + (category != null ? "("
            + category[0] + ")}" : "}");
    }

    private String getParsedActivity(String fieldHeader, String activity)
    {
        return "\\columntitle{" + fieldHeader + "} & \\activity{" + activity + "}";
    }

    private String getParsedSingleItem(String fieldHeader, String item)
    {
        return getParsedColumnSubTitle(fieldHeader) + " & \\singleitem{" + item + "}\\\\\n";
    }

    private String getParsedColumnSubTitle(String fieldHeader)
    {
        return "\t\\columnsubtitle{" + fieldHeader + "}";
    }

    private String getParsedNewLineList(String title, List<String> items)
    {
        return getParsedList(title, items, "newlinelist");
    }

    private String getParsedCommaSeparatedList(String title, List<String> items)
    {
        return getParsedList(title, items, "commaseparatedlist");
    }

    private String getParsedList(String title, List<String> items, String texListTag)
    {
        StringBuilder result =
            new StringBuilder(getParsedColumnSubTitle(title)).append(" & \\").append(texListTag).append("{");
        Iterator<String> iterator = items.iterator();
        while (iterator.hasNext())
        {
            result.append(iterator.next()).append(iterator.hasNext() ? "," : "");
        }
        return result.append("}\\\\\n").toString();
    }
}
