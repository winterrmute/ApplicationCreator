package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.model.Career;
import com.wintermute.applicationcreator.model.CategoryGroup;
import com.wintermute.applicationcreator.model.Language;
import com.wintermute.applicationcreator.model.Project;
import com.wintermute.applicationcreator.model.Skill;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generates description how to handle tags which should be replaced through generated document parts.
 *
 * @author wintermute
 */
public class ReplaceTagHandlerGenerator
{

    private final DocumentContentParser contentParser;

    public ReplaceTagHandlerGenerator()
    {
        contentParser = new DocumentContentParser();
    }

    /**
     * @param target information to replace the placeholder.
     * @return function replacing the placeholder with provided data.
     */
    public Function<String, String> createInlineEntry(String target)
    {
        return s -> s.replace(s, target);
    }

    /**
     * @param target list of languages.
     * @return function replacing the placeholder with provided data.
     */
    public Function<String, String> createLanguageEntries(List<Language> target)
    {
        List<String> languageEntry = target
            .stream()
            .map(l -> "\n\t\\columnsubtitle{" + l.getLanguage() + "} & " + "\\singleitem{" + l.getLevelDesc() + "}\\\\")
            .collect(Collectors.toList());

        return createMultiLineEntry(languageEntry);
    }

    /**
     * @param target list of projects
     * @return function replacing the placeholder with prepared projects.
     */
    public Function<String, String> createProjectEntries(Map<CategoryGroup, List<Project>> target)
    {
        return createEntryForCategory(target, "Projects", false);
    }

    private <T> Function<String, String> createEntryForCategory(Map<CategoryGroup, List<T>> target, String section,
                                                                boolean isTable)
    {
        StringBuilder result = contentParser.parseEntryWithCategory(target, section, isTable);
        return s -> s.replace(s, result.toString());
    }

    /**
     * @param target list of projects
     * @return function replacing the placeholder with prepared projects.
     */
    public Function<String, String> createCareerEntries(Map<CategoryGroup, List<Career>> target)
    {
        return createEntryForCategory(target, "Career", true);
    }

    /**
     * @param target list of skills
     * @return function replacing the placeholder with prepared skills.
     */
    public Function<String, String> createSkillsEntries(Map<CategoryGroup, List<Skill>> target)
    {
        return s -> s.replace(s, contentParser.parseSkills(target));
    }

    private Function<String, String> createMultiLineEntry(List<String> target)
    {
        StringBuilder documentContent = new StringBuilder();
        return s ->
        {
            target.forEach(t -> documentContent.append(s).append("\n"));
            return documentContent.toString();
        };
    }
}
