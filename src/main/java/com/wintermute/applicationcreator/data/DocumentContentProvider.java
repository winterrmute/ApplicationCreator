package com.wintermute.applicationcreator.data;

import com.wintermute.applicationcreator.model.Career;
import com.wintermute.applicationcreator.model.CategoryGroup;
import com.wintermute.applicationcreator.model.Language;
import com.wintermute.applicationcreator.model.Project;
import com.wintermute.applicationcreator.model.Skill;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Provides parsed content to embed into tex template.
 *
 * @author wintermute
 */
public class DocumentContentProvider
{

    private final DocumentContentParser contentParser;

    public DocumentContentProvider()
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
     * @return function replacing the placeholder with provided languages.
     */
    public Function<String, String> createLanguageEntries(List<Language> target)
    {
        return s -> s.replace(s, contentParser.getParsedLanguages(target));
    }

    /**
     * @param target list of projects grouped by category
     * @return function replacing the placeholder with prepared projects.
     */
    public Function<String, String> createProjectEntries(Map<CategoryGroup, List<Project>> target)
    {
        return createEntryForCategory(target, "Projects", false);
    }

    /**
     * @param target list of career grouped by category.
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
        return s -> s.replace(s, contentParser.getParsedSkills(target));
    }

    private <T> Function<String, String> createEntryForCategory(Map<CategoryGroup, List<T>> target, String section,
                                                                boolean isTable)
    {
        StringBuilder result = contentParser.getParsedContentGroupedByCategory(target, section, isTable);
        return s -> s.replace(s, result.toString());
    }
}
